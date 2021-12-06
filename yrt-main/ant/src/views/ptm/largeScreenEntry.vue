<template>
    <div>
        <div class="largeScreenEntryBackground">
            <img class="largeScreenEntryBackgroundImg" src="/images/largeScreen/大屏.png" />
        </div>
        <div class="largeScreenEntryButton">
            <a-button ghost @click="entryClick"> 进入大屏 </a-button>
        </div>
    </div>
</template>

<script>
export default {
    methods: {
        entryClick() {
            this.$router.replace({ path: '/largeScreen/index' });
        }
    }
};
</script>

<style>
.largeScreenEntryBackgroundImg {
    height: 100%;
    width: 100%;
    filter: blur(10px);
}

.largeScreenEntryButton {
    position: absolute;
    top: 50%;
    right: 50%;
    transform: translate(50%, -50%);
}

.largeScreenEntryButton button {
    height: 100px;
    width: 200px;
    font-size: 30px;
    font-weight: 600;
    background-color: #11111100;
    color: #fff;
    border: solid;
    border-radius: 20px;
}
</style>
