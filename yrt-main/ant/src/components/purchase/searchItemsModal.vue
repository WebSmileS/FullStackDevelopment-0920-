<template>
    <div class="modal">
        <div class="modal-content">
            <header class="modal-header">
                <h2 class="modal-title">搜索: {{ modalHeading }}</h2>
                <button type="button" class="close" tabindex="-1" @click="close">x</button>
            </header>
            <main class="modal-main">
                <div class="control-panel">
                    <div class="even-columns">
                        <div class="col">
                            <input name="search" class="input" placeholder="搜索" type="text" v-model="searchTerm" />
                            <!-- @keyup="filterItems" -->
                        </div>
                        <button type="button" class="search" @click="searchProducts">Search</button>
                    </div>
                </div>
                <div class="content">
                    <table class="table-contents">
                        <thead>
                            <th v-for="column of columns" class="table-heading" :style="{ width: column.width + 'px' }">
                                {{ column.name }}
                            </th>
                        </thead>
                        <tbody>
                            <template v-for="(item, index) of itemsToShow">
                                <tr class="table-row" @click="itemClicked(item, index, originalItemIndex)">
                                    <td class="table-cell" v-for="value of item">{{ value }}</td>
                                    <!-- v-if="searchStringMatches(item[Object.keys(item)[0]])" -->
                                </tr>
                            </template>
                        </tbody>
                    </table>
                </div>
            </main>
            <footer class="modal-footer">
                <vxe-pager
                    style="margin: 0 22px"
                    :loading="page.loading"
                    :current-page="page.current"
                    :page-size="page.pageSize"
                    :page-sizes="page.pageSizes"
                    :total="page.total"
                    :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                    @page-change="changePage"
                />
            </footer>
        </div>
    </div>
</template>

<script>
import _ from 'lodash';

export default {
    name: 'search-items-modal',
    props: ['modalHeading', 'columns', 'items', 'picked', 'originalItemIndex', 'page', 'onChangePage', 'onSearch'],
    data() {
        return {
            searchTerm: '',
            filtered: false,
            triggerSearch: true,
            vendorBackup: []
        };
    },
    methods: {
        close() {
            this.$emit('close');
            this.resetSearchTerm();
        },
        searchProducts() {
            // this.changePage({})
        },
        itemClicked(item, index, originalItemIndex) {
            console.log('Picked item: ');
            console.log(item);
            console.log(index);
            console.log(originalItemIndex);
            this.close();
            this.picked(item, index, originalItemIndex);
        },
        changePage({ currentPage, pageSize }) {
            this.onChangePage({ currentPage, pageSize });
        },
        resetSearchTerm() {
            this.searchTerm = '';
            this.triggerSearch = false;
        },
        debouncedSearch: _.debounce(function (val) {
            this.onSearch(val);
        }, 500)
    },
    computed: {
        itemsToShow() {
            let tempItems = [];
            let val = this.searchTerm;

            if (val && val !== '') {
                const collator = new Intl.Collator('zh-Hans');
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
            } else {
                tempItems = this.items.slice();
            }
            return tempItems;
        }
    },
    watch: {
        searchTerm(val) {
            if (this.triggerSearch) {
                this.debouncedSearch(val);
            } else {
                this.triggerSearch = true;
            }
        }
    }
};
</script>

<style scoped>
.modal {
    width: 100%;
    height: 100%;
    margin: 48px auto;
    z-index: 5;
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.4);
    text-align: left;
}

.modal-content {
    background: #ffffff;
    box-shadow: 2px 2px 20px 1px;
    display: flex;
    flex-direction: column;
    margin: auto;
    padding: 2em auto;
    width: 80%;
    max-width: 600px;
    max-height: 90%;
}

.modal-header,
.modal-footer {
    display: flex;
    padding: 1rem 1rem;
    justify-content: space-between;
    align-items: flex-start;
    border-bottom: 1px solid #dee2e6;
    max-height: 100px;
}

.modal-main {
    /* position: absolute; */
    width: 100%;
    overflow-y: auto;
}

.modal-title {
    line-height: 1.5;
    font-weight: 400;
    color: #212529;
}

.btn-close {
    position: absolute;
    top: 0;
    right: 0;
    border: none;
    font-size: 20px;
    padding: 10px;
    cursor: pointer;
    font-weight: bold;
    color: #4aae9b;
    background: transparent;
}

.btn-green {
    color: white;
    background: #4aae9b;
    border: 1px solid #4aae9b;
    border-radius: 2px;
}

.close {
    /* padding: 1rem 1rem; */
    /* margin: -1rem -1rem -1rem auto; */
    background-color: transparent;
    text-shadow: 0 1px 0 #ffffff;
    font-weight: 700;
    font-size: 1rem;
    /* float: right; */
    cursor: pointer;
    border: none;
}

.control-panel {
    padding: 1em;
    border-bottom: 1px solid #cccccc;
}

.table-contents {
    margin: 0 8px;
    font-size: 0.8rem;
    table-layout: fixed;
    width: 100%;
}

.table-heading {
    color: #666666;
    padding: 13px 4px;
}

.table-cell {
    border-top: 1px solid #dee2e6;
    padding: 6px 4px;
}

.table-row:hover {
    cursor: pointer;
    background-color: #f5f5f5;
}
.search {
    background-color: #1890ff;
    padding: 0.24em;
    border: none;
    border-radius: 1px;
    box-shadow: 0 4px 8px 0 rgba(71, 71, 71, 0.2), 0 6px 20px 0 rgba(73, 73, 73, 0.19);
    color: white;
    max-width: 4em;
    cursor: pointer;
}

.search:hover {
    background-color: #0072dd;
}
</style>
