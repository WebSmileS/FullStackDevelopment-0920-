<template>
    <div
        class="and-menu"
        :style="{ top: menu.position.top + 'px', left: menu.position.left + 'px' }"
    >
        <ul class="am-ul">
            <li
                class="amu-li"
                v-for="m in menu.data"
                :class="{ 'disabled': m.disabled }"
                :key="m.key"
            >
                {{ m.name }}
            </li>
        </ul>
    </div>
</template>

<script>
export default {
    name: 'AndMenu',
    props: {
        menu: {
            type: Object,
            default: () => {}
        }
    },
    data() {
        return {

        };
    },
    computed: {
    }
};
</script>

<style lang="less">
.and-menu {
    position: absolute;
    background-color: white;
    border: 1px solid #ccc;
    border-bottom-width: 2px;
    border-right-width: 2px;
    z-index: 10;
    .am-ul {
        padding: 0;
        margin: 0;
        min-width: 150px;
        .amu-li {
            height: 30px;
            color: #373737;
            padding: 5px 20px;
            text-align: left;
            &.disabled {
                color: #999;
            }
            &:hover:not(.disabled) {
                background-color: #f0f0f0;
            }
        }
    }
}
</style>