<template>
  <div>
      <productContractGrid :gridType="'ProContract'" :refUrl="'index'"/>
  </div>
</template>

<script>
import productContractGrid from '../grid/commonGridTest.vue'

export default {
    components: {
        productContractGrid
    },

}
</script>

<style>

</style>