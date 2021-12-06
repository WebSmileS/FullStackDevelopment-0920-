<template>
    <div>
        <div class="header">
            <div>
                <img class="headerIcon" src="/images/largeScreen/icon/icon44_blue.png">
                <span class="headerTitle">机构数量统计图</span>
            </div>
            <button v-if="!cityGridVisible && provinceVisible" class="index-back-country-map-button" @click="onBackToCountry">返回全国地图</button>
            <button v-if="cityGridVisible && !provinceVisible" class="index-back-country-map-button" @click="onBackToProvince">返回全省地图</button>
        </div>
        <cityData v-show="cityGridVisible && !provinceVisible"/>
        <div v-show="!cityGridVisible && provinceVisible">
            <img src="/images/largeScreen/icon/路径 212.png" id="iconImgProvince" @click="onCityClick">
            <img src="/images/largeScreen/icon/云南地图.png" id="mapImgProvince">
        </div>
        <div v-show="!cityGridVisible && !provinceVisible" class="background">
            <img src="/images/largeScreen/icon/路径 192.png" id="iconImg" @click="onProvinceClick">
            <img src="/images/largeScreen/img/map_num.png" id="mapImg">
        </div>
    </div>
</template>

<script>
import cityData from '../components/tables/cityData.vue'

    export default {
        components: {
            cityData
        },
        data() {
            return {
                provinceVisible: false,
                cityGridVisible: false
            }
        },
        methods: {
            onBackToCountry () {
                this.provinceVisible = false
                this.cityGridVisible = false
                this.$emit('provinceVisible', false)
            },
            onBackToProvince () {
                this.provinceVisible = true
                this.cityGridVisible = false
                this.$emit('provinceVisible', true)
                this.$emit('cityGridVisible', false)
            },
            onProvinceClick () {
                this.provinceVisible = true
                this.cityGridVisible = false
                this.$emit('provinceVisible', true)
            },
            onCityClick () {
                this.provinceVisible = false
                this.cityGridVisible = true
                this.$emit('cityGridVisible', true)
                document.getElementById('capsuleItemDefault').focus()
            }
        }
    }
</script>

<style scoped>
    .header {
        padding-top: 0.24rem;
        padding-left: 0.32rem;
        display: flex;
    }

    .headerIcon {
        height: 0.26rem;
    }

    .headerTitle {
        margin-top: auto;
        margin-bottom: auto;
        margin-left: 0.24rem;
        width: 2.1rem;
        height: 0.42rem;
        font-size: 0.3rem;
        font-family: PingFang SC, PingFang SC-Regular;
        font-weight: 400;
        text-align: left;
        color: #00ffff;
        line-height: 0.36rem;
    }

    .background {
        height: 10rem;
        background-size: 20rem;
        background-position: center;
        background-repeat: no-repeat;
        background-image: url('/images/largeScreen/img/中国地图@2x.png');
    }

    #mapImgProvince {
        height: 12rem;
        position: absolute;
        left: -5rem;
        top: -0.1rem;
    }

    #mapImg {
        height: 8rem;
        margin-left: 1rem;
    }

    #iconImgProvince {
        position: absolute;
        height: 1.53rem;
        opacity: 0.1;
        z-index: 10;
        top: 3.8rem;
        left: 6.65rem;
    }

    #iconImgProvince:hover {
        opacity: 0.7;
    }

    #iconImg {
        position: absolute;
        top: 6.05rem;
        height: 1.53rem;
        left: 5.53rem;
        opacity: 0.1;
    }

    #iconImg:hover {
        opacity: 0.7;
    }

    .numContainer {
        z-index: 100;

    }

    .num {
        width: 0.22rem;
        height: 0.27rem;
        opacity: 1;
        font-size: 0.20rem;
        font-family: PingFang-SC-Medium;
        text-align: left;
        color: #ffffff;
        line-height: 0.54rem;
    }

    .index-back-country-map-button {
        width: 200px;
        height: 42px;
        border-radius: 8px;
        background: rgb(0, 113, 188, 0.35);
        color: white;
        z-index: 100;
        position: absolute;
        left: 8.5rem;
        border: none;
    }

    .index-back-country-map-button:hover {
        background: rgb(0, 113, 188, 0.65);
    }
</style>