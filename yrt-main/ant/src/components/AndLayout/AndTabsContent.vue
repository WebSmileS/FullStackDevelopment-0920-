<template>
    <a-row class="and-tabs-content">
        <a-row class="atc-radio-btns">
            <a-radio-group :value="cur" @change="changeTab">
                <a-radio-button v-for="pane in panes" :key="pane.key" :value="pane.key">
                    {{ pane.title }}
                </a-radio-button>
            </a-radio-group>
        </a-row>
        <a-row :class="'atc-content ' + cur + '-atc-content'">
            <slot name="atc-header"></slot>
            <slot name="atc-pane"></slot>
            <slot name="atc-footer"></slot>
        </a-row>
    </a-row>
</template>

<script>
/**
 * Consists of Breadcrumb, Tabs Content, Modals
 */
export default {
    name: 'AndTabsContent',
    props: {
        panes: {
            type: Array,
            default: () => [
                [
                    { key: 'add', title: '新增' },
                    { key: 'history', title: '历史' }
                ]
            ]
        },
        cur: {
            type: String,
            default: () => 'add'
        }
    },
    data() {
        return {

        };
    },
    methods: {
        changeTab(e) {
            this.$emit('changeTabFn', e.target.value);
        },
    },
    computed: {

    }
};
</script>