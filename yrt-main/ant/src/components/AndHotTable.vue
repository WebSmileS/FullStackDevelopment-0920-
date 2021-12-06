<template>
    <div ref="andHotTable"></div>
</template>

<script>
import Handsontable from 'handsontable/dist/handsontable.min.js';

export default {
    name: 'AndHotTable',
    props: {
        data: {
            type: Object,
            required: true
        }
    },
    data() {
        let self = this;

        return {
            _table: null,
            defaultSettings: {
                debug: true
            },
            settings: null
        }
    },
    mounted() {
        this.setupSettings();
        this.initTable();

        this.$emit('hot-table-mounted', this);
    },
    methods: {
        initTable() {
            this._table = new Handsontable(this.$refs.table, this.settings);
        },
        destroyTable() {
            this.$emit('changed', this._table.getData());
            this._table.destroy();
            this._table = void 0;
        },
        setupSettings() {
            const $this = this;
        
            this.settings = Object.assign(this.defaultSettings, {
                rowHeaders: this.data.rowHeaders,
                colHeaders: this.data.colHeaders,
                data: this.data.data,
                afterDestroy: () => this.$emit('destroyed'),
                afterChange: function() {
                    $this.$emit('changed', this.getData());
                },
            });
        }
    }
};
</script>