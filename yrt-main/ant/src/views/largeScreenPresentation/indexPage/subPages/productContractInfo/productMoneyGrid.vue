<template>
  <div>
      <commonGrid :gridType="'ProMoney'" :refUrl="'productInfo'"/>
  </div>
</template>

<script>
import commonGrid from '../grid/commonGrid.vue'

export default {
    components: {
        commonGrid
    },

}
</script>

<style>

</style>