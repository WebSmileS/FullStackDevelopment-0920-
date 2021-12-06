<template>
    <div class="and-hottable-acts">
        <div class="and-ha-hammer" @click="dropdown">
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none">
                <path opacity="0.5" d="M12.5657 9.63427L16.75 5.44995C17.1642 5.03574 17.8358 5.03574 18.25 5.44995C18.6642 5.86416 18.6642 6.53574 18.25 6.94995L12.7071 12.4928C12.3166 12.8834 11.6834 12.8834 11.2929 12.4928L5.75 6.94995C5.33579 6.53574 5.33579 5.86416 5.75 5.44995C6.16421 5.03574 6.83579 5.03574 7.25 5.44995L11.4343 9.63427C11.7467 9.94669 12.2533 9.94668 12.5657 9.63427Z" fill="currentColor"/>
                <path d="M12.5657 15.6343L16.75 11.45C17.1642 11.0357 17.8358 11.0357 18.25 11.45C18.6642 11.8642 18.6642 12.5357 18.25 12.95L12.7071 18.4928C12.3166 18.8834 11.6834 18.8834 11.2929 18.4928L5.75 12.95C5.33579 12.5357 5.33579 11.8642 5.75 11.45C6.16421 11.0357 6.83579 11.0357 7.25 11.45L11.4343 15.6343C11.7467 15.9467 12.2533 15.9467 12.5657 15.6343Z" fill="currentColor"/>
            </svg>              
        </div>
        <div class="and-ha-dropdown" v-if="dropdownVisible">
            <div class="and-had-option">
                <a-checkbox :value="storeFixedWidths" @change="onFixWidthsChange">
                    保存列宽调整
                </a-checkbox>
            </div>
            <div class="and-had-border"/>
                <div class="and-had-group">
                    <a-checkbox-group @change="onChange" :default-value="page.colHeaders && page.colHeaders.length > 0 ? page.colHeaders : data.colHeaders">
                        <div
                            v-for="(header, idx) in data.colHeaders" 
                            :key="idx"
                            class="and-had-option"
                        >
                            <a-checkbox :value="header">
                                {{ header }}
                            </a-checkbox>
                        </div>
                    </a-checkbox-group>
                </div>
            <div class="and-had-border"/>
            
            <div class="and-had-acts">
                <a-col :span="12" @click="store">
                    确认
                </a-col>
                <a-col :span="12" @click="revert">
                    还原
                </a-col>
            </div>
        </div>
    </div>
</template>

<script>

export default {
    name: 'AndHotTableActs',
    props: {
        data: {
            type: Object,
            default: () => {}
        },
        pageName: {
            type: String,
            default: () => ''
        },
        curTab: {
            type: String,
            default: () => ''
        },
        hotRef: null
    },
    data() {
        return {
            dropdownVisible: false,
            cookieInfo: this.$cookie,
            page: {
                colHeaders: [],
                widths: []
            },
            storeFixedWidths: false
        }
    },
    methods: {
        dropdown() {
            this.dropdownVisible = !this.dropdownVisible;
        },
        revert() {
            this.page = {
                colHeaders: [],
                widths: []
            };
            this.cookieInfo.delete(this.pageName + '-' + this.curTab);
            this.$emit('renderPage', this.curTab);
            this.dropdown();
        },
        onFixWidthsChange(e) {
            this.storeFixedWidths = e.target.checked;
        },
        onChange(checkedValues) {
            this.page = {
                ...this.page,
                colHeaders: checkedValues
            };
        },
        store() {
            let colHeaders = [];
            this.data.colHeaders.forEach((item) => {
                if (this.page.colHeaders.includes(item)) {
                    colHeaders.push(item);
                }
            });
            let data = {
                colHeaders: colHeaders
            };
            if (this.storeFixedWidths && this.hotRef !== null) {
                const _this = this;
                if (this.page.colHeaders.length === 0) {
                    data['widths'] = this.data.colHeaders.map((item, idx) => {
                        return _this.hotRef.getColWidth(idx);
                    });
                } else {
                    data['widths'] = this.page.colHeaders.map((item, idx) => {
                        return _this.hotRef.getColWidth(idx);
                    });
                }
            }
            this.cookieInfo.set(this.pageName + '-' + this.curTab, JSON.stringify(data));
            this.dropdown();
            this.$emit('renderPage', this.curTab);
        },
        refresh(pageName, curTab) {
            // load page settings
            if (pageName && curTab) {
                let cookieData = JSON.parse(this.cookieInfo.get(pageName + '-' + curTab));
                if (cookieData !== null) {
                    if (cookieData.colHeaders) {
                        this.page.colHeaders = cookieData.colHeaders;
                    }
                    if (cookieData.widths) {
                        this.page.widths = cookieData.widths;
                    }
                } else {
                    this.page = {
                        colHeaders: [],
                        widths: []
                    };
                }
            }
        }
    },
    mounted() {
        this.refresh(this.pageName, this.curTab);
    },
    watch: {
        curTab: function(newV, oldV) {
            this.refresh(this.pageName, newV);
        }
    }
};
</script>