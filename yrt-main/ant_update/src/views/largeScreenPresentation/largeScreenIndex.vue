<template>
    <div id="largeScreenApp">
        <div>
            <!-- 头部标题 -->
            <div class="headTitle">
                <img src="/images/largeScreen/img/头@2x.png" width="100%">
            </div>
            <router-view>
            </router-view>
        </div>
    </div>
</template>

<script>

</script>

<style>
#largeScreenBackButton {
    position: absolute;
    top: 10px;
    left: 10px;
    height: 40px;
    width: 120px;
    font-size: 20px;
    font-weight: 400;
    background-color: #11111100;
    color: #ffffffdd;
    border: solid;
    border-radius: 20px;
}

.headTitle {
  position: absolute;
  width: 100%;
}

.leftPart {
  position: absolute;
  top: 1.04rem;
  left: 0.36rem;
  width: 6.34rem;
  height: 13rem;
}

.middlePart {
  position: absolute;
  top: 1.04rem;
  left: 6.9rem;
  width: 11.8rem;
  height: 13rem;
}

.rightPart {
  position: absolute;
  top: 1.04rem;
  right: 0.36rem;
  width: 6.34rem;
  height: 13rem;
  }
</style>
