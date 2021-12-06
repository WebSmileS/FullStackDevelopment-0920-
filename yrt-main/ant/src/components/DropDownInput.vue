<template>
    <div class="dropDownComponent">
        <input
            type="text"
            class="myInput"
            v-model="inputVal"
            @click="showDropdown"
            v-on:focus="showDropdown"
            v-on:blur="hideDropdown"
            @input="$emit('input', $event.target.value)"
        />

        <div
            class="dropDownContent"
            :style="{ display: dropdownDisplayStyle }"
            v-on:mouseover="hover = true"
            v-on:mouseleave="hover = false"
        >
            <div v-for="(item, index) in itemsToShow" @click="onSelect(item, index)" :class="getItemClass(item.name)">
                {{ item.name }}
            </div>
        </div>
    </div>
</template>

<script>
import _ from 'lodash';

export default {
    name: 'DropDownInput',
    props: ['items', 'picked', 'searchMorePicked', 'value', 'originalIndex', 'search'],
    computed: {
        inputVal: {
            get() {
                return this.value;
            },
            set(val) {
                this.$emit('input', val);
            }
        },
        itemsToShow: function () {
            let tempItems = [];
            let val = this.inputVal;

            if (val && val !== '') {
                const collator = new Intl.Collator('zh-Hans');
                console.log('Filtering');
                for (let i = 0; i < this.items.length; i++) {
                    let startsWith = true;
                    for (let j = 0; j < val.length; j++) {
                        if (collator.compare(val.charAt(j), this.items[i].name.charAt(j)) !== 0) {
                            startsWith = false;
                            break;
                        }
                    }
                    if (startsWith) {
                        tempItems.push(this.items[i]);
                    }
                }

                if (tempItems.length > 5) {
                    tempItems.splice(5);
                }
            } else {
                tempItems = this.items.slice(0, 5);
            }
            tempItems.push({ id: -1, name: this.searchMoreString });
            tempItems.push({ id: -2, name: this.startTypingString });
            console.log(tempItems);
            return tempItems;
        }
    },
    watch: {
        inputVal: function (val) {
            this.debouncedSearch(val);
        }
    },
    components: {},
    data() {
        return {
            dropdownDisplayStyle: 'none',
            dropDownClass: {
                display: this.dropdownDisplayStyle
            },
            searchMoreClass: {
                padding: '0.2rem 1.5rem',
                color: '#1890ff'
            },
            startTypingClass: {
                padding: '0.2rem 1.5rem',
                'font-style': 'italic'
            },
            hover: false,
            searchMoreString: '搜索更多...',
            startTypingString: '提示：输入文字搜索'
        };
    },
    methods: {
        onSelect: function (item, index) {
            console.log('Selected item: ');
            console.log(item);
            if (item.name !== this.startTypingString) {
                this.hover = false;
                this.hideDropdown();
                console.log(item);
                if (item.name === this.searchMoreString) {
                    this.searchMorePicked(this.originalIndex);
                    console.log(this.originalIndex);
                    console.log(item);
                } else {
                    // this.inputVal = item.name;
                    this.picked(item, index, this.originalIndex);
                    console.log(item);
                }
            }
        },
        showDropdown: function () {
            this.dropdownDisplayStyle = 'block';
        },
        hideDropdown: function () {
            if (!this.hover) {
                this.dropdownDisplayStyle = 'none';
            }
        },
        getItemClass: function (name) {
            if (name === this.searchMoreString) {
                return 'searchMore';
            } else if (name === this.startTypingString) {
                return 'startTyping';
            } else {
                return 'dropDownItem';
            }
        },
        debouncedSearch: _.debounce(function (val) {
            if (this.search) {
                this.search(val);
            }
        }, 500)
    }
};
</script>

<style scoped>
.dropDownComponent {
    position: relative;
    display: inline-block;
}

.myInput {
    cursor: pointer;
    border-top-style: none;
    border-left-style: none;
    border-right-style: none;
    border: none;
    display: inline-block;
    width: 100%;
    outline: none;
}

.dropDownContent {
    border-left: 1px solid #c5c5c5;
    border-right: 1px solid #c5c5c5;
    border-bottom: 1px solid #c5c5c5;
    overflow-y: auto;
    overflow-x: none;
    max-height: 15rem;
    z-index: 1;
    position: absolute;
    width: 85%;
    background-color: white;
}

.dropDownItem {
    padding: 0.2rem 0.8rem;
    text-decoration: none;
    display: inherit;
    color: #545f54;
    font-weight: normal;
}

.searchMore {
    padding: 0.2rem 1.5rem;
    color: #1890ff;
}

.dropDownItem:hover,
.searchMore:hover {
    background-color: #e9ecef;
    cursor: pointer;
}

.startTyping {
    padding: 0.2rem 1.5rem;
    font-style: italic;
}
</style>
