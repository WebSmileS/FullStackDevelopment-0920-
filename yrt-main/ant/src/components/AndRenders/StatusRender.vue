<template>
    <div><i style="color: #a9a9a9">Row: {{row}}, column: {{col}},</i> value: {{value}}</div>
</template>

<script>
/**
 * Consists of Breadcrumb, Tabs Content, Modals
 */
export default {
    name: 'StatusRender',
    data() {
        return {
            hotInstance: null,
            TD: null,
            row: null,
            col: null,
            prop: null,
            value: null,
            cellProperties: null
        }
    },
    computed: {
    }
};
</script>