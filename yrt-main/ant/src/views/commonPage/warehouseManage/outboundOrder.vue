<template>
    <my-list :myListPropObj="myListPropObj"></my-list>
</template>

<script>
import myList from './modal/outPutStorageMange';
export default {
    name: 'outboundOrder',
    data() {
        return {
            myListPropObj: {
                pageType: 1 // 页面类型判断依据： 0-入库单   1-出库单
            }
        };
    },
    components: {
        myList
    }
};
</script>

<style lang="less" scoped></style>
