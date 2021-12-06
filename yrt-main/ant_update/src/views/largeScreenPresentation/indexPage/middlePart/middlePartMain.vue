<template>
    <div>
        <div>
            <div class="middleUpPart">
                <middleUpPart 
                    @provinceVisible="onProvinceVisible" 
                    @cityGridVisible="onCityGridVisible"
                />
            </div>
            <div class="middleDownPart">
                <middleDownPart 
                    :ProvinceVisible="isProvinceVisible"
                    :CityGridVisible="isCityGridVisible"
                />
            </div>
        </div>
    </div>
</template>

<script>
import middleUpPart from './middleUpPart'
import middleDownPart from './middleDownPart'


export default {
    name: 'middlePart',
    components: {
        middleUpPart,
        middleDownPart
    },
    data () {
        return {
            orgAreaMap: {},
            orgTypeMap: {},
            isProvinceVisible: false,
            isCityGridVisible: false
        }
    },
    methods: {
        init (middlePartData) {
            this.orgAreaMap = middlePartData.ocList
            this.orgTypeMap = middlePartData.otList
        },
        onProvinceVisible (isVisible) {
            this.isProvinceVisible = isVisible
        },
        onCityGridVisible (isVisible) {
            this.isCityGridVisible = isVisible
        }
    },
    mounted () {
    }
}
</script>

<style>

.middleUpPart {
  height: 8.6rem;
  width: 11.8rem;
}

.middleDownPart {
  margin-top: 0.24rem;
  margin-bottom: 0.32rem;
  height: 4.16rem;
  width: 11.8rem;
}

</style>
