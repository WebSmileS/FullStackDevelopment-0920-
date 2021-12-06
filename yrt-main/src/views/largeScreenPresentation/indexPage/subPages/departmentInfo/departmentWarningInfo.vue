<template>
  <div>
      <departmentWarningGrid :gridType="'DepWarning'" :refUrl="'index'"/>
  </div>
</template>

<script>
import departmentWarningGrid from '../grid/orgTest.vue'

export default {
    components: {
        departmentWarningGrid
    },

}
</script>

<style>

</style>