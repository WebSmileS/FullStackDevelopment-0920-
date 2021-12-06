<template>
  <div>
      <commonGrid :gridType="'ProBatch'" :refUrl="'productInfo'"/>
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