<template>
    <div>
        <div v-if="!CityGridVisible">
            <div class="capsuleContainer">
                <div class="capsuleItem" style="background: linear-gradient(90deg,#fba993, #ef77a8);">
                    <img class="capsuleImg" src="/images/largeScreen/icon/组 96@2x.png">
                    <div class="capsuleDesc">
                        <div class="capsuleName">生产商</div>
                        <div class="capsuleQuantity">18,246</div>
                    </div>
                </div>
                <div class="capsuleItem" style="background: linear-gradient(90deg,#facd20, #fbb03b);">
                    <img class="capsuleImg" src="/images/largeScreen/icon/组 98@2x.png">
                    <div class="capsuleDesc">
                        <div class="capsuleName">经销商</div>
                        <div class="capsuleQuantity">18,246</div>
                    </div>
                </div>
                <div class="capsuleItem" style="background: linear-gradient(90deg,#0fe18a, #00a99d);">
                    <img class="capsuleImg" src="/images/largeScreen/icon/组 101@2x.png">
                    <div class="capsuleDesc">
                        <div class="capsuleName">医院</div>
                        <div class="capsuleQuantity">18,246</div>
                    </div>
                </div>
                <div class="capsuleItem" style="background: linear-gradient(90deg,#0fe1d9, #29abe2);">
                    <img class="capsuleImg" src="/images/largeScreen/icon/组 115@2x.png">
                    <div class="capsuleDesc">
                        <div class="capsuleName">科室</div>
                        <div class="capsuleQuantity">18,246</div>
                    </div>
                </div>
                <div class="capsuleItem" style="background: linear-gradient(90deg,#b98dff, #7f58e6);">
                    <img class="capsuleImg" src="/images/largeScreen/icon/组 116@2x.png">
                    <div class="capsuleDesc">
                        <div class="capsuleName">回收商</div>
                        <div class="capsuleQuantity">18,246</div>
                    </div>
                </div>
            </div>
            <div class="gridContainer">
                <mapDataTable :isProvince="ProvinceVisible"/>
            </div>
        </div>
    </div>
</template>

<script>
import mapDataTable from '../components/tables/mapDataTable'
export default {
    components: {
        mapDataTable
    },
    props: {
        ProvinceVisible: Boolean,
        CityGridVisible: Boolean
    }
}
</script>

<style>
.capsuleContainer {
    padding-left: 0.42rem;
    display: flex;
    flex-direction: row;
}

.capsuleItem {
    position: relative;
    margin-right: 0.24rem;
    width: 2rem;
    height: 0.66rem;
    border-radius: 0.18rem;
}

.capsuleImg {
    position: absolute;
    left: 0.25rem;
    top: 0.1rem;
    width: 0.47rem;
    height: 0.41rem;
    
}

.capsuleDesc {
    position: absolute;
    left: 0.72rem;
    width: 1.28rem;
    padding-top: 0.05rem;
}

.capsuleName {
    margin-left: auto;
    margin-right: auto;
    width: 0.60rem;
    height: 0.27rem;
    font-size: 0.20rem;
    font-family: PingFang-SC-Medium;
    text-align: center;
    color: #ffffff;
    line-height: 0.24rem;
}

.capsuleQuantity {
    position: absolute;
    top: 0.18rem;
    left: 0.2rem;
    margin-top: 0.02rem;
    width: 0.76rem;
    height: 0.35rem;
    opacity: 1;
    font-size: 0.26rem;
    font-family: SFProDisplay-Medium;
    text-align: center;
    color: #ffffff;
    line-height: 0.53rem;
}

.gridContainer {
    margin-top: 0.24rem;
    height: 3.26rem;
    width: 11.80rem;
    background-color: #0071bc2e;
}
</style>
