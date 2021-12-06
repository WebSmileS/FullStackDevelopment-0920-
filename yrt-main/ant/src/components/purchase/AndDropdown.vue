<template>
    <div class="and-dropdown" :style="'display:' + obj.display + ';left:' + obj.left + 'px;top:' + obj.top + 'px;width:' + obj.width + 'px'">
        <div class="and-dropdown-options">
            <div v-for="d in obj.data" :key="d.key" :data_key="d.key" class="and-dropdown-option" @mousedown="chooseVendorFn">
                {{ d.name }}
            </div>
            <div 
                class="and-dropdown-option"
                style="color: #1890ff" 
                data_key="search-more"
                @mousedown="chooseVendorFn"
            >
                搜索更多...
            </div>
            <div
                class="and-dropdown-option"
                style="font-style: italic"
                data_key="tips"
                @mousedown="chooseVendorFn"
            >
                提示：输入文字搜索
            </div>
        </div>
    </div>
</template>

<script>

export default {
    name: 'AndDropdown',
    props: {
        obj: {
            type: Object,
            default: () => {}
        }
    },
    data() {
        return {
        }
    },
    methods: {
        chooseVendorFn(e) {
            let value = e.target.getAttribute('data_key');
            if (value === 'search-more') {
                this.$emit('openAddProTb');
                return;
            } else if (value === 'tips') {
                return;
            } else {
                this.$emit('chooseProFn', value);
            }
        }
    },
    mounted() {
    }
};
</script>