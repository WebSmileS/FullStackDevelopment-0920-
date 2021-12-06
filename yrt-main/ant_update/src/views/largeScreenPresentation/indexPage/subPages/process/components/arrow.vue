<template>
    <div>
        <img 
            :src="isLeft ? leftArrowPath : rightArrowPath"
            class="processArrow"
        >
    </div>
</template>

<script>
export default {
    props: {
        isLeft: Boolean 
    },
    data () {
        return {
            // 左箭头路径
            leftArrowPath: '/images/largeScreen/icon/箭头-1.png',
            // 右箭头路径
            rightArrowPath: '/images/largeScreen/icon/箭头.png'
        }
    }

}
</script>

<style>
.processArrow {
    position: relative;
    height: 0.78rem;
    margin-bottom: 1rem;
}
</style>
