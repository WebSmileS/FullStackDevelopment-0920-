<template functional>
    <a-sub-menu :key="props.menuInfo.id.toString()">
        <span slot="title">
            <i :class="props.menuInfo.runes" class="yrt"></i>
            <span>{{ props.menuInfo.name }}</span>
        </span>
        <template v-for="item in props.menuInfo.children">
            <a-menu-item v-if="item.children && !item.children.length" :key="item.id.toString()">
                <i :class="item.runes" class="yrt"></i>
                <span>{{ item.name }}</span>
            </a-menu-item>
            <sub-menu v-else :key="item.id.toString()" :menu-info="item" />
        </template>
    </a-sub-menu>
</template>
<script>
export default {
    name: 'SubTree',
    props: ['menuInfo']
};
</script>
