<template>
  <div>
      <productWarningGrid :gridType="'ProWarning'" :refUrl="'index'"/>
  </div>
</template>

<script>
import productWarningGrid from '../grid/commonGrid.vue'

export default {
    components: {
        productWarningGrid
    },

}
</script>

<style>

</style>