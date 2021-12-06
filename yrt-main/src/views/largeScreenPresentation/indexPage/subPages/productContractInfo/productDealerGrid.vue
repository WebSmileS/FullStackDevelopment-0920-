<template>
  <div>
      <commonGrid :gridType="'ProDealer'" :refUrl="'productInfo'"/>
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