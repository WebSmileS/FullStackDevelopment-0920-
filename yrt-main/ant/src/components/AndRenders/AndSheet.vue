<template>
    <div
        class="and-sheet"
        @keydown="tdKeydown"
        @scroll="onSheetScroll"
    >
        <div class="as-table">
            <div class="as-body">
                <div
                    class="asb-row asbr-header"
                >
                    <div
                        class="asbr-td ast-head"
                        :class="{ 'as-row-header': j === 0, 'highlight': selected.column === j }"
                        v-for="(col, j) in columns"
                        :key="col.data"
                        :style="{ width: colWidths[j] + 'px' }"
                    >
                        {{ col.name }}
                        <div
                            v-if="j !== 0"
                            class="asth-r"
                            @mousedown="asthrMousedown($event, j)"
                        ></div>
                    </div>
                </div>
                <div
                    class="asb-row"
                    :class="{ 'selected': selection.on === true && i >= selection.start.row && i <= selection.end.row }"
                    v-for="(d, i) in data"
                    :key="i"
                >
                    <div
                        class="asbr-td"
                        v-for="(col, j) in columns"
                        :key="j"
                        :class="{
                            'ast-head as-row-header': j === 0,
                            'highlight': (selected.row === i && j === 0),
                            'selected': (selected.on && selected.row === i && j === selected.column),
                            'asbrt-btop-bottom': (selection.on && selection.oneRow && selection.start.row === i && j !== 0 && j !== selection.end.column),
                            'asbrt-btop-bottom-right': (selection.on && selection.oneRow && selection.start.row === i && j === selection.end.column),
                            'asbrt-btop': (selection.on && !selection.oneRow && j !== 0 && i === selection.start.row),
                            'asbrt-bbottom': (selection.on && !selection.oneRow && j !== 0 && i === selection.end.row),
                            'asbrt-bright': (selection.on && !selection.oneRow && j === selection.end.column && i < selection.end.row && i > selection.start.row),
                            'asbrt-btop-right': (selection.on && !selection.oneRow && j === selection.end.column && i === selection.start.row),
                            'asbrt-bbottom-right': (selection.on && !selection.oneRow && j === selection.end.column && i === selection.end.row),
                            'disabled': j !== 0 && col.disabled
                        }"
                        @click="tdClicked($event, i, j)"
                        @dblclick="tdDblClick($event, i, j)"
                        @contextmenu="openMenu($event, i, j)"
                        :tabindex="i.toString() + '9' + j.toString()"
                        :style="{ width: colWidths[j] + 'px' }"
                        @mousedown="asbrtheadMouseDown($event, i, j)"
                        :data_row="i"
                        :data_column="j"
                    >
                        <!-- <div
                            v-if="editable.on === false || editable.row !== i || editable.column !== j"
                            class="asbrt-view"
                            :style="[ d['style'] && d['style'].color ? { width: (colWidths[j] - 4) + 'px', color: d['style'].color } : { width: (colWidths[j] - 4) + 'px' } ]"
                            :data_row="i"
                            :data_column="j"
                        >{{ d[col.data] }}</div> -->
                        <div
                            v-if="editable.on === false || editable.row !== i || editable.column !== j"
                            class="asbrt-view"
                            :style="[ d['style'] && d['style'].color ? { width: (colWidths[j] - 4) + 'px', color: d['style'].color } : { width: (colWidths[j] - 4) + 'px' } ]"
                            :data_row="i"
                            :data_column="j"
                        >{{ getTdValue(i, j) }}</div>
                        <!-- input -->
                        <a-input
                            v-if="col.type === 'input' && editable.on === true && editable.row === i && editable.column === j"
                            :placeholder="d[col.data]"
                            @change="inputChange($event, i, j)"
                            autoFocus
                        />
                        <!-- numeric input -->
                        <a-input
                            v-if="(col.type === 'number' || col.type === 'minusNumber') && editable.on === true && editable.row === i && editable.column === j"
                            type="number"
                            :placeholder="d[col.data]"
                            @keydown="validateNumber"
                            @change="inputChange($event, i, j)"
                            autoFocus
                        />
                        <!-- cascader -->
                        <a-cascader
                            v-if="col.type === 'cascader' && editable.on === true && editable.row === i && editable.column === j && d[col.obj.options].data !== null"
                            v-model="d[col.obj.options].key"
                            :options="d[col.obj.options].data"
                            expandTrigger="hover"
                            @change="onCascaderChange"
                            :displayRender="displayRender"
                        />
                        <!-- simpleDropdown -->
                        <a-input
                            v-if="col.type === 'simpleDropdown' && editable.on === true && editable.row === i && editable.column === j"
                            v-model="d[col.data]"
                            type="number"
                            @keydown="validateNumber"
                            @change="dropdownChange(i, j)"
                            autoFocus
                        />
                        <div
                            v-if="col.type === 'simpleDropdown' && editable.on === true && editable.row === i && editable.column === j"
                            class="and-dropdown"
                            :data_left="editable.position.top + 'px'"
                            :style="{ top: editable.position.top + 'px', left: editable.position.left + 'px', width: (colWidths[j] - 2) + 'px' }"
                        >
                            <div class="and-dropdown-options">
                                <div
                                    v-for="(d, idx) in col.list"
                                    class="and-dropdown-option"
                                    :class="{ 'hover': dropdownPos === idx }"
                                    :key="d.key"
                                    @mousedown="chooseDropdown($event, i, j, d.key)"
                                    @mousemove="mousemoveDropdown($event, idx)"
                                >
                                    {{ d.name }}
                                </div>
                            </div>
                        </div>
                        <!-- dropdown -->
                        <a-input
                            v-if="col.type === 'dropdown' && editable.on === true && editable.row === i && editable.column === j"
                            v-model="d[col.data]"
                            @change="dropdownChange(i, j)"
                            autoFocus
                        />
                        <div
                            v-if="col.type === 'dropdown' && editable.on === true && editable.row === i && editable.column === j"
                            class="and-dropdown"
                            :data_left="editable.position.top + 'px'"
                            :style="{ top: editable.position.top + 'px', left: editable.position.left + 'px', width: (colWidths[j] - 2) + 'px' }"
                        >
                            <div class="and-dropdown-options">
                                <div
                                    v-for="(d, idx) in dropdownData"
                                    class="and-dropdown-option"
                                    :class="{ 'hover': dropdownPos === idx }"
                                    :key="d.key"
                                    @mousedown="chooseDropdown($event, i, j, d.key)"
                                    @mousemove="mousemoveDropdown($event, idx)"
                                >
                                    {{ d.name }}
                                </div>
                                <div 
                                    class="and-dropdown-option"
                                    :class="{ 'hover': dropdownPos === dropdownData.length }"
                                    style="color: #1890ff" 
                                    key="search-more"
                                    @mousedown="chooseDropdown($event, i, j, 'search-more')"
                                    @mousemove="mousemoveDropdown($event, 10)"
                                >
                                    搜索更多...
                                </div>
                                <div
                                    class="and-dropdown-option"
                                    :class="{ 'hover': dropdownPos === dropdownData.length + 1 }"
                                    style="font-style: italic"
                                    key="tips"
                                    @mousedown="chooseDropdown($event, i, j, 'tips')"
                                    @mousemove="mousemoveDropdown($event, 11)"
                                >
                                    提示：输入文字搜索
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div
                    v-if="totalFormat.on"
                    class="asb-row asb-row-footer"
                >
                    <div
                        class="asbr-td"
                        v-for="(col, j) in columns"
                        :key="j"
                    >
                        <div
                            v-if="j === 0"
                            :style="{ width: colWidths[j] + 'px'}"
                        >合计</div>
                        <div
                            v-else-if="totalFormat.footer.length > 0 && totalFormat.footer.includes(col.data)"
                            :style="{ width: (colWidths[j] - 4) + 'px'}"
                        >
                            {{ sum(col.data) }}
                        </div>
                        <div
                            v-else
                            :style="{ width: (colWidths[j] - 4) + 'px'}"
                        ></div>
                    </div>
                </div>
            </div>
        </div>
        <div class="empty-body" v-if="data.length === 0">
            <p>暂无数据可显示</p>
        </div>
        <div
            v-if="resize.visible"
            class="ast-draggable-line"
            :class="{ 'active': resize.idx !== undefined }"
            :style="{ left: (resize.left + resize.dx).toString() + 'px', height: resize.height + 'px' }"
        ></div>
        <div
            class="and-menu"
            :class="{ 'block': menu.visible }"
            tabindex="32727"
            :style="{ top: menu.position.top + 'px', left: menu.position.left + 'px' }"
        >
            <ul
                class="am-ul"
            >
                <li
                    class="amu-li"
                    v-for="m in menu.data"
                    :class="{ 'disabled': m.disabled }"
                    :key="m.key"
                    @click="clickedMenu(m.key)"
                >
                    {{ m.name }}
                </li>
            </ul>
        </div>
        <div
            v-if="totalFormat.searchable && searchable.on"
            class="and-searchable"
        >
            <div class="search-body">
                <div class="sb-search">
                    <a-input-search
                        :disabled="searchTables.flag"
                        placeholder="搜索..."
                        @search="onSearch"
                        @keydown="enterPrevent($event)"
                    />
                </div>
                <div class="sb-acts" @click="closeSearch">
                    <a-icon
                        class="search-close"
                        type="close"
                    />
                </div>
                <div class="sb-acts" @click="showDeepSearch">
                    <a-icon
                        v-if="!searchTables.flag"
                        class="deep-down"
                        type="down-circle"
                    />
                    <a-icon
                        v-if="searchTables.flag"
                        class="deep-down"
                        type="up-circle"
                    />
                </div>
            </div>
            <div
                v-if="searchFields.length && searchTables.flag"
                class="search-full"
            >
                <div
                    v-for="sf in searchFields"
                    :key="sf.data"
                    class="sf-row"
                >
                    <div
                        v-if="sf.type === 'input'"
                        class="sfr-item"
                    >
                        <a-input
                            v-model="searchTables[sf.data]"
                            type="text"
                            :placeholder="sf.title"
                        />
                    </div>
                    <div
                        v-if="sf.type === 'tree-select'"
                        class="sfr-item"
                    >
                        <a-tree-select
                            treeNodeFilterProp="title"
                            :showSearch="true"
                            :treeData="sf.list"
                            :dropdownStyle="{ maxHeight: '250px', overflow: 'auto' }"
                            treeDefaultExpandAll
                            v-model="searchTables[sf.data]"
                            allowClear
                            @change="changeSearchTreeSelect"
                            class="input"
                            style="min-width: 120px"
                            :placeholder="sf.title"
                        >
                        </a-tree-select>
                    </div>
                    <div
                        v-if="sf.type === 'select'"
                        class="sfr-item"
                    >
                        <a-select
                            v-model="searchTables[sf.data]"
                            type="text"
                            allowClear
                            :options="sf.list"
                            :placeholder="sf.title"
                        >
                        </a-select>
                    </div>
                </div>
                <div
                    class="sf-row"
                >
                    <a-button
                        @click="deepSearch"
                        size="small"
                    >搜索</a-button>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
function replaceAll(string, search, replace) {
  return string.split(search).join(replace);
}

export default {
    name: 'AndSheet',
    props: {
        columns: {
            type: Array,
            default: () => []
        },
        data: {
            type: Array,
            default: () => []
        },
        widths: {
            type: Array,
            default: () => []
        },
        menu: {
            type: Object,
            default: () => {}
        },
        dropdownData: {
            type: Array,
            default: () => []
        },
        totalFormat: {
            type: Object,
            default: () => function () {
                return {
                    on: false,
                    searchable: false,
                    formula: [],
                    footer: []
                }
            }
        },
        searchFields: {
            type: Array,
            default: () => []
        }
    },
    data() {
        return {
            cookieInfo: this.$cookie,
            selected: {
                on: false,
                row: undefined,
                column: undefined
            },
            selection: {
                on: false,
                oneRow: false,
                start: {
                    row: undefined,
                    column: undefined
                },
                end: {
                    row: undefined,
                    column: undefined
                },
                down: {
                    row: undefined,
                    column: undefined
                }
            },
            resize: {
                x: 0,
                dx: 0,
                left: 0,
                height: 0,
                visible: false,
                idx: undefined
            },
            editable: {
                on: false,
                row: undefined,
                column: undefined,
                position: {
                    left: 0,
                    top: 0
                }
            },
            dropdownPos: undefined,
            searchable: {
                on: true,
                obj: {
                    name: ''
                }
            },
            searchTables: {
                flag: false
            }
        };
    },
    methods: {
        sum(key) {
            let sum = 0.0;
            this.data.forEach((d) => {
                if (d[key] !== '') {
                    sum += parseFloat(d[key]);
                }
            });
            return sum;
        },
        onSheetScroll(e) {
            if ((e.target.scrollHeight - e.target.clientHeight - e.target.scrollTop) < 20) {
                this.$emit('scrollToEnd');
            }
        },
        focusCellByRowAndColumn(row, column) {
            document.querySelectorAll('[tabindex="' + row.toString() + '9' + column.toString() + '"]')[0].focus();
        },
        calcEditablePos(row, column) {
            if (this.columns[column].type === 'dropdown' || this.columns[column].type === 'simpleDropdown') {
                this.editable.position = {
                    left: parseInt(document.querySelectorAll('[tabindex="' + row.toString() + '9' + column.toString() + '"]')[0].offsetLeft - document.querySelectorAll('[tabindex="' + row.toString() + '9' + column.toString() + '"]')[0].closest('.and-sheet').scrollLeft),
                    top: parseInt(document.querySelectorAll('[tabindex="' + row.toString() + '9' + column.toString() + '"]')[0].offsetTop - document.querySelectorAll('[tabindex="' + row.toString() + '9' + column.toString() + '"]')[0].closest('.and-sheet').scrollTop) + 26
                };
            }
        },
        selectCell(row, column, on = true) {
            this.selected = {
                on: on,
                row,
                column
            };
        },
        // stop event
        stopEvent(e) {
            if (e !== null) {
                e.preventDefault();
                e.stopPropagation();
            }
        },
        selectRow(row) {
            this.selection = {
                on: true,
                oneRow: true,
                start: {
                    row,
                    column: 1
                },
                end: {
                    row,
                    column: this.columns.length - 1
                }
            };
            // select
            this.selectCell(row, 1);
        },
        // tables events
        tdClicked(e, row, column) {
            if (this.editable.on === false || this.columns[column].type !== 'cascader') {
                this.menu.visible = false;
                this.selection.on = false;
                this.editable.on = false;
                if (this.editable.on === false) {
                    if (column === 0) {
                        this.selectRow(row);
                        this.focusCellByRowAndColumn(row, column);
                        // prevent
                        this.stopEvent(e);
                    } else {
                        this.selectCell(row, column);
                        this.focusCellByRowAndColumn(row, column);
                        if (this.editable.on === false) {
                            // prevent
                            this.stopEvent(e);
                        }
                    }
                }
            }
        },
        // keyboard
        prevCell() {
            this.selected.column = this.selected.column > 1 ? this.selected.column - 1 : this.selected.column;
        },
        nextCell() {
            if (this.selected.column === (this.columns.length - 1)) {
                if (this.selected.row === this.data.length - 1) {
                    this.downCell('enter');
                } else {
                    this.selected.row = this.selected.row + 1;
                    this.selected.column = 1;
                }
            } else {
                this.selected.column = (this.columns.length - 1) > this.selected.column ? this.selected.column + 1 : this.selected.column;
            }
        },
        downCell(type = 'arrow-down') {
            if (type === 'enter' && this.selected.row === this.data.length - 1) {
                this.$emit('addNewRow');
            } else {
                this.selected.row = (this.data.length - 1) > this.selected.row ? this.selected.row + 1 : this.selected.row;
            }
        },
        upCell() {
            this.selected.row = this.selected.row > 0 ? this.selected.row - 1 : this.selected.row;
        },
        tdKeydown(e) {
            if (e.ctrlKey && e.keyCode === 70) {
                this.searchable.on = true;
                this.stopEvent(e);
                return;
            }
            if (this.editable.on === false && this.selected.on) {
                if ([9, 37, 38, 39, 40].includes(e.keyCode)) {
                    switch (e.keyCode) {
                        case 37:
                            this.prevCell();                        
                            break;
                        case 38:
                            this.upCell();
                            break;
                        case 39:
                        // case 9:
                            this.nextCell();
                            break;
                        case 40:
                            this.downCell();
                            break;
                        // ********
                        case 9:
                            this.downCell('enter');
                            break;
                    }
                    this.focusCellByRowAndColumn(this.selected.row, this.selected.column);
                    // prevent
                    this.stopEvent(e);
                } else {
                    if (e.keyCode === 13) {
                        this.columns[this.selected.column].disabled === false && this.enterCell(null, this.selected.row, this.selected.column);
                        this.columns[this.selected.column].disabled === true && this.nextCell();
                        // prevent
                        this.stopEvent(e);
                        return;
                    }
                }
            }
            if (this.editable.on === true) {
                // dropdown
                if (this.columns[this.editable.column].type === 'dropdown' || this.columns[this.editable.column].type === 'simpleDropdown') {
                    if (e.keyCode === 13 || e.keyCode === 38 || e.keyCode === 40) {
                        // prevent
                        this.stopEvent(e);
                        // checking arrow up/down/enter
                        if (e.keyCode === 13) {
                            this.focusCellByRowAndColumn(this.selected.row, this.selected.column);
                            this.editable.on = false;
                            let customNext = this.columns[this.editable.column].customNext;
                            if (this.dropdownPos !== undefined) {
                                let key;
                                if (this.columns[this.editable.column].type === 'dropdown') {
                                    switch (this.dropdownPos) {
                                        case this.dropdownData.length:
                                            key = 'search-more';
                                            break;
                                        case this.dropdownData.length + 1:
                                            key = 'tips';
                                            break;
                                        default:
                                            key = this.dropdownData[this.dropdownPos].key;
                                            break;
                                    }
                                } else {
                                    key = this.columns[this.editable.column].list[this.dropdownPos].key;
                                }
                                this.chooseDropdown(null, this.editable.row, this.editable.column, key);
                                if (customNext) {
                                    return;
                                }
                            }
                            // ********
                            this.nextCell();
                            return;
                        } else {
                            if (this.columns[this.editable.column].type === 'dropdown') {
                                switch (e.keyCode) {
                                    case 38:
                                        this.dropdownPos = this.dropdownPos === undefined || this.dropdownPos === 0 ? 0 : this.dropdownPos - 1;
                                        break;
                                    case 40:
                                        this.dropdownPos = this.dropdownPos === undefined ? 0 : (this.dropdownPos < this.dropdownData.length + 1 ? this.dropdownPos + 1 : this.dropdownPos);
                                        break;
                                }
                            } else {
                                switch (e.keyCode) {
                                    case 38:
                                        this.dropdownPos = this.dropdownPos === undefined || this.dropdownPos === 0 ? 0 : this.dropdownPos - 1;
                                        break;
                                    case 40:
                                        this.dropdownPos = this.dropdownPos === undefined ? 0 : (this.dropdownPos < this.columns[this.editable.column].list.length - 1 ? this.dropdownPos + 1 : this.dropdownPos);
                                        break;
                                }
                            }
                            let cusPos = (this.dropdownPos + 1) * 35;
                            if (cusPos > 150) {
                                document.getElementsByClassName('and-dropdown')[0].scrollTop = (this.dropdownPos - 3) * 35;
                            } else {
                                document.getElementsByClassName('and-dropdown')[0].scrollTop = 0;
                            }
                        }
                    }
                }
                // sheet
                switch (e.keyCode) {
                    case 13:
                        this.selection.on = false;
                        this.editable.on = false;
                        this.menu.visible = false;
                        // ********
                        // this.downCell('enter');
                        this.nextCell();
                        // prevent
                        this.stopEvent(e);
                        // focus
                        this.focusCellByRowAndColumn(this.selected.row, this.selected.column);
                        break;
                    case 9:
                        this.selection.on = false;
                        this.editable.on = false;
                        this.menu.visible = false;
                        // ********
                        // this.nextCell();
                        this.downCell('enter');
                        // prevent
                        this.stopEvent(e);
                        // focus
                        this.focusCellByRowAndColumn(this.selected.row, this.selected.column);
                        break;
                }
            }
        },
        // enter cell
        enterCell(e, row, column) {
            if (this.columns[column].disabled === true) return;
            if (column === 0) return;
            this.selectCell(row, column, true);
            this.editable = {
                on: true,
                row,
                column,
                position: {
                    left: undefined,
                    top: undefined
                }
            };
            this.calcEditablePos(row, column);
            // prevent
            if (e !== null) {
                this.stopEvent(e);
            }
        },
        tdDblClick(e, row, column) {
            this.menu.visible = false;
            this.selection.on = false;
            this.editable.on = false;
            if (this.editable.on === false) {
                this.enterCell(e, row, column);
            }
        },
        editableBlur(row, column) {
            this.editable = {
                on: false,
                row,
                column,
                position: {
                    left: undefined,
                    top: undefined
                }
            };
        },
        rowHeaderBlur(e, row, column) {
            if (column === 0) {
                this.selection = {
                    on: false,
                    start: {
                        row: undefined,
                        column: undefined
                    },
                    end: {
                        row: undefined,
                        column: undefined
                    }
                };
            }
            this.stopEvent(e);
        },
        // right click to open menu
        openMenu(e, i, j) {
            // prevent
            this.stopEvent(e);
            if (e.button === 2) {
                if (j === 0) {
                    if (this.selection.on === false || i < this.selection.start.row || i > this.selection.end.row) {
                        // select row
                        this.selection.oneRow = true;
                        this.selectRow(i);
                        this.$emit('multiSelected', {
                            from: i,
                            to: i
                        });
                    }
                    // menu settings
                    let sheetRect = e.target.closest('.and-sheet').getBoundingClientRect();
                    let dx = 0,
                        menuHeight = 30 * this.menu.data.length;
                    if (document.body.clientHeight - e.clientY < menuHeight + 80) {
                        dx = menuHeight;
                    }
                    this.menu.position.top = e.clientY - sheetRect.y - dx;
                    this.menu.position.left = e.clientX - sheetRect.x;
                    this.menu.visible = true;
                }
            }
        },
        closeMenu() {
            this.menu.visible = false;
        },
        // resizable header
        asthrMousedown(e, j) {
            // clear
            this.menu.visible = false;
            this.selection.on = false;
            this.editable.on = false;
            // func
            e.target.focus();
            this.resize.x = e.clientX;
            this.resize.idx = j;
            this.resize.height = e.target.closest('.and-sheet').offsetHeight > (this.data.length * 26 + 28) ? (this.data.length * 26 + 28) : e.target.closest('.and-sheet').offsetHeight;
            // calc left
            this.resize.left = e.target.offsetLeft + 2;
            // Attach the listeners to `document`
            document.addEventListener('mousemove', this.asthrMousemove);
            document.addEventListener('mouseup', this.asthrMouseup);
            // prevent
            this.stopEvent(e);
        },
        asthrMousemove(e) {
            if (this.resize.visible === false) {
                this.resize.visible = true;
            }
            const dx = e.clientX - this.resize.x;
            this.resize.dx = dx;
        },
        asthrMouseup(e) {
            const dx = e.clientX - this.resize.x;
            this.resize.dx = dx;
            this.resize.left += dx;
            if (this.resize.idx !== undefined) {
                this.colWidths[this.resize.idx] = this.colWidths[this.resize.idx] + this.resize.dx;
            }
            // clear resize idx
            this.resize.idx = undefined;
            this.resize.visible = false;
            // Remove the listeners to `document`
            document.removeEventListener('mousemove', this.asthrMousemove);
            document.removeEventListener('mouseup', this.asthrMouseup);
            // prevent
            this.stopEvent(e);
            // change widths to parent
            this.$emit('changeWidths', this.colWidths);
        },
        // multi rows
        asbrtheadMouseDown(e, row, column) {
            // check th-head and left click
            if (column === 0 && e.button === 0) {
                // clear
                this.menu.visible = false;
                this.selection.on = true;
                this.editable.on = false;
                // func
                e.target.focus();
                // selection
                this.selection.down = true;
                this.selection.start = {
                    row,
                    column: 1
                };
                this.selection.down = {
                    ...this.selection.start
                };
                this.selection.end = {
                    row,
                    column: this.columns.length - 1
                };
                this.selection.oneRow = true;
                // select
                this.selectCell(row, 1);
                // Attach the listeners to `document`
                document.addEventListener('mousemove', this.asbrtheadMousemove);
                document.addEventListener('mouseup', this.asbrtheadMouseup);
                // prevent
                this.stopEvent(e);
            }
        },
        uptSelectionObj(e) {
            let row = parseInt(e.target.getAttribute('data_row'));
            if (this.selection.down.row > row) {
                this.selection.end = {
                    ...this.selection.end,
                    row: this.selection.down.row
                };
                this.selection.start = {
                    ...this.selection.start,
                    row
                };
            } else {
                this.selection.start = {
                    ...this.selection.down
                };
                this.selection.end = {
                    ...this.selection.end,
                    row: parseInt(e.target.getAttribute('data_row'))
                };
            }
            if (this.selection.start.row === this.selection.end.row) {
                this.selection.oneRow = true;
            } else {
                this.selection.oneRow = false;
            }
        },
        asbrtheadMousemove(e) {
            this.uptSelectionObj(e);
        },
        asbrtheadMouseup(e) {
            this.uptSelectionObj(e);
            // fire selected multiple
            this.$emit('multiSelected', {
                from: this.selection.start.row,
                to: this.selection.end.row
            });
            // Remove the listeners to `document`
            document.removeEventListener('mousemove', this.asbrtheadMousemove);
            document.removeEventListener('mouseup', this.asbrtheadMouseup);
        },
        // right click
        clickedMenu(key) {
            this.selection.on = false;
            this.menu.visible = false;
            this.editable.on = false;
            this.selected.on = false;
            this.$emit('clickedMenu', {
                selection: {
                    from: this.selection.start.row,
                    to: this.selection.end.row
                },
                key
            });
        },
        // com func
        dropdownChange(i, j) {
            if (this.columns[j].type === 'simpleDropdown') {
                this.data[i][this.columns[j].data] = this.data[i][this.columns[j].data];
                if (this.columns[j].data.includes('_string')) {
                    let cData = this.columns[j].data;
                    cData = cData.replace('_string', '');
                    this.data[i][cData] = parseFloat(this.data[i][this.columns[j].data]);
                }
            }
            this.$emit('searchComData', {
                type: this.columns[j].type,
                name: this.columns[j].data,
                value: this.data[i][this.columns[j].data]
            });
        },
        chooseDropdown(e, i, j, key) {
            if (e) {
                this.stopEvent(e);
            }
            if (this.columns[j].type === 'simpleDropdown') {
                this.data[i][this.columns[j].data] = key;
                if (this.columns[j].data.includes('_string')) {
                    let cData = this.columns[j].data;
                    cData = cData.replace('_string', '');
                    this.data[i][cData] = parseFloat(key);
                }
            } else {
                this.$emit('chooseComData', {
                    type: this.columns[j].type,
                    name: this.columns[j].data,
                    row: i,
                    value: key
                });
            }
            this.editable.on = false;
            this.dropdownPos = undefined;
            this.focusCellByRowAndColumn(i, j);
        },
        mousemoveDropdown(e, idx) {
            this.dropdownPos = idx;
            // prevent
            this.stopEvent(e);
        },
        // allow float number field
        validateNumber(e) {
            // validate phone number
            !((e.keyCode > 47 && e.keyCode < 58) ||
            (e.keyCode > 95 && e.keyCode < 106) ||
            e.keyCode === 37 ||
            e.keyCode === 39 ||
            e.keyCode === 9 ||
            e.keyCode === 8 ||
            e.keyCode === 46 ||
            e.keyCode === 36 ||
            e.keyCode === 35 ||
            e.keyCode === 190 ||
            e.keyCode === 110 ||
            e.keyCode === 13) && e.preventDefault();
        },
        // input change
        inputChange(e, row, col) {
            this.data[row][this.columns[col].data] = e.target.value;
            if (this.columns[col].data.includes('_string')) {
                let cData = this.columns[col].data;
                cData = cData.replace('_string', '');
                this.data[row][cData] = parseFloat(e.target.value);
            }
        },
        prefixAndSubffix(data, subffix = undefined, prefix = undefined) {
            if (subffix) {
                data = data + subffix;
            }
            if (prefix) {
                data = prefix + data;
            }
            return data;
        },
        getTdValue(row, col) {
            if (this.data[row][this.columns[1].data] !== '') {
                if (this.columns[col].type === 'minusNumber') {
                    if (this.data[row][this.columns[col].data] > 0) {
                        return this.prefixAndSubffix(0 - this.data[row][this.columns[col].data], this.columns[col].subffix);
                    } else {
                        return this.prefixAndSubffix(this.data[row][this.columns[col].data], this.columns[col].subffix);
                    }
                }
                if (this.columns[col].formula) {
                    let tpFormula = this.columns[col].formula;
                    const reCheckVar = /[a-z_$][a-z_$]*/g;
                    const sfVars = [...tpFormula.matchAll(reCheckVar)];
                    let invalid = false;
                    sfVars.forEach((cv) => {
                        if (this.data[row][cv] === '') {
                            invalid = true;
                        } else {
                            let val = this.data[row][cv] ? parseFloat(this.data[row][cv]) : 0;
                            tpFormula = replaceAll(tpFormula, '"' + cv + '"', val);
                        }
                    });
                    let val = eval(tpFormula).toFixed(4);
                    this.data[row][this.columns[col].data] = invalid ? '' : (val === 'NaN' || val === undefined ? '' : val );
                    return this.prefixAndSubffix(this.data[row][this.columns[col].data], this.columns[col].subffix);
                }
                return this.prefixAndSubffix(this.data[row][this.columns[col].data], this.columns[col].subffix);
            } else {
                return this.data[row][this.columns[col].data];
            }
        },
        enterPrevent(e) {
            if (e.keyCode === 13) {
                this.stopEvent(e);
            }
        },
        // search
        onSearch(name) {
            this.$emit('searchSheet', {
                flag: false,
                name
            });
        },
        closeSearch() {
            this.searchable.on = false;
        },
        changeFocus(row, column) {
            this.enterCell(null, row, column);
        },
        // custom cacaser render
        displayRender(obj) {
            let len = obj.labels.length;
            const label = obj.labels[len - 1];
            return label;
        },
        onCascaderChange(value) {
            this.data[this.selected.row][this.columns[this.selected.column].obj.vModel] = value[value.length - 1];
            this.data[this.selected.row][this.columns[this.selected.column].obj.options].key = value;
            if (value.length > 0) {
                let data = this.data[this.selected.row][this.columns[this.selected.column].obj.options].data.map((item) => { return item; }),
                    selectedName = '';
                value.forEach((val) => {
                    let obj = [];
                    data.forEach((item) => {
                        if (item.value === val) {
                            obj = item;
                            selectedName = item.name;
                        }
                    });
                    data = obj.children;
                });
                this.data[this.selected.row][this.columns[this.selected.column].data] = selectedName;
            } else {
                this.data[this.selected.row][this.columns[this.selected.column].data] = '';
            }
        },
        // deep search funcs
        showDeepSearch() {
            this.searchTables.flag = !this.searchTables.flag;
        },
        changeSearchTreeSelect(value, label) {
            // this.searchTables[sf.data] = value && value.length ? value[0] : '';
            this.searchTables.department_inner_sn = value && value.length ? value[0] : '';
        },
        deepSearch() {
            this.$emit('searchSheet', this.searchTables);
        }
    },
    computed: {
        colWidths() {
            let widths = [];
            if (this.widths.length === 0) {
                this.columns.forEach((column, idx) => {
                    if (idx === 0) {
                        widths.push(50);
                    } else {
                        widths.push(150);
                    }
                });
            } else {
                this.columns.forEach((c, idx) => {
                    if (this.widths[idx]) {
                        widths.push(this.widths[idx])
                    } else {
                        widths.push(150);
                    }
                });
            }
            return widths;
        }
    }
};
</script>

<style lang="less">
.and-sheet {
    width: 100%;
    height: 100%;
    overflow-y: scroll;
    font-size: 13px;
    cursor: default;
    .empty-body {
        color: #373737;
        p {
            line-height: 100px;
        }
    }
    .as-table {
        display: table;
        border-collapse: collapse;
        
        .asbr-td,
        .ash-th {
            display: table-cell;
            border: 1px solid #ccc;
            &.selected {
                box-shadow: inset 0px 0px 0px 2px #4b89ff !important;
            }
            &:focus {
                outline: none;
            }
        }
        .as-header {
            display: table-header-group;
            background-color: #f0f0f0;
            .ash-th {
                color: #222;
                height: 27px;
                line-height: 27px;
                .asth-r {
                    cursor: col-resize;
                    height: 100%;
                    float: right;
                    width: 6px;
                }
            }
        }
        .as-body {
            display: table-row-group;
            .asb-row {
                display: table-row;
                &.selected {
                    .asbr-td:first-child {
                        background-color: #8eb0e7;
                    }
                    .asbr-td {
                        background-color: #e6efff;
                    }
                }
                .asbr-td {
                    color: #373737;
                    height: 25px;
                    line-height: 25px;
                    // start - selection border styles
                    &.asbrt-btop {
                        box-shadow: inset 0px 1px 0px 0px #4b89ff;
                    }
                    &.asbrt-bbottom {
                        box-shadow: inset 0px -1px 0px 0px #4b89ff;
                    }
                    &.asbrt-bright {
                        box-shadow: inset -1px 0px 0px 0px #4b89ff;
                    }
                    &.asbrt-btop-bottom {
                        box-shadow: inset 0px 1px 0px 0px #4b89ff, inset 0px -1px 0px 0px #4b89ff;
                    }
                    &.asbrt-btop-bottom-right {
                        box-shadow: inset 0px 1px 0px 0px #4b89ff, inset 0px -1px 0px 0px #4b89ff, inset -1px 0px 0px 0px #4b89ff;
                    }
                    &.asbrt-btop-right {
                        box-shadow: inset 0px 1px 0px 0px #4b89ff, inset -1px 0px 0px 0px #4b89ff;
                    }
                    &.asbrt-bbottom-right {
                        box-shadow: inset 0px -1px 0px 0px #4b89ff, inset -1px 0px 0px 0px #4b89ff;
                    }
                    // end - selection border styles
                    .asbrt-elem {
                        display: none;
                        width: 100%;
                        height: calc(100% - 2px);
                        border: none;
                    }
                    .block {
                        display: block;
                    }
                    .none {
                        display: none;
                    }
                    & > div.asbrt-view {
                        user-select: none;
                        -moz-user-select: none;
                        -webkit-user-select: none;
                        -ms-user-select: none;
                        text-overflow: ellipsis;
                        overflow: hidden;
                        white-space: nowrap;
                        margin-left: 2px;
                    }
                    /* Chrome, Safari, Edge, Opera */
                    input::-webkit-outer-spin-button,
                    input::-webkit-inner-spin-button {
                        -webkit-appearance: none;
                        margin: 0;
                    }

                    /* Firefox */
                    input[type=number] {
                        -moz-appearance: textfield;
                    }
                    .ant-cascader-picker {
                        width: calc(100% - 4px);
                        .ant-cascader-input {
                            border: none !important;
                        }
                    }
                }
            }
        }
        .asbr-header {
            height: 28px;
            position: sticky;
            top: 0;
            box-shadow: 0px -1px 0px 0px #ccc, inset 0px -1px 0px 0px #ccc;
            .asbr-td {
                height: 27px !important;
                line-height: 27px !important;
            }
            .asth-r {
                cursor: col-resize;
                height: 100%;
                float: right;
                width: 6px;
            }
        }
        .ast-head {
            color: #222;
            background-color: #f0f0f0;
            &.highlight {
                background-color: #dcdcdc;
            }
        }
        .as-row-header {
            width: 50px !important;
            background-color: #f0f0f0;
            color: #222;
        }
        .asb-row-footer {
            position: sticky;
            bottom: 0;
            height: 28px;
            background: white;
            box-shadow: 0px -2px 0px 0px #ccc, inset 0px -1px 0px 0px #ccc;
            font-weight: 600;
            .asbr-td {
                line-height: 28px;
                color: #ff0000;
                &:first-child {
                    background: #ff0000;
                    color: #fff;
                }
            }
        }
    }
    .ast-draggable-line {
        position: absolute;
        top: 0;
        height: 100%;
        &.active {
            width: 2px;
            border-right: 1px solid #cccccc;
        }
    }
    input {
        width: calc(100% - 4px);
        height: calc(100% - 4px) !important;
        padding: 1px;
        border: none;
        &:focus, &:active {
            outline: none;
        }
    }
    .asbrt-dropdown {
        display: none;
        &.block {
            display: block;
        }
    }
    // ant style
    .ant-select-selection {
        border-bottom: none;
    }
    .and-searchable {
        .search-full {
            z-index: 1010;
            position: absolute;
            top: -9px;
            right: -9px;
            width: 300px;
            border-top-left-radius: 5px;
            border-bottom-left-radius: 5px;
            border: 1px solid #ccc;
            background: white;
            padding: 15px 0 10px 0;
            .sf-row {
                margin-top: 5px;
                height: 30px;
                .sfr-item {
                    & > :first-child {
                        margin-top: 3px;
                    }
                }
                &:not(:last-child):hover {
                    background-color: #f1f8ff;
                }
                &:first-child {
                    margin-top: 0;
                }
                input {
                    height: 24px !important;
                    padding: 0 11px;
                }
                &:last-child {
                    height: 24px;
                    text-align: right;
                    button {
                        margin-right: 10px;
                    }
                }
            }
        }
        .search-body {
            z-index: 1010;
            position: absolute;
            top: -50px;
            right: -9px;
            width: 300px;
            height: 37px;
            padding: 5px 0 5px 5px;
            border-top-left-radius: 5px;
            border-bottom-left-radius: 5px;
            border: 1px solid #ccc;
            background: white;
            .sb-search {
                float: left;
                width: calc(100% - 55px);
                height: 25px;
                input {
                    line-height: 25px;
                    height: 25px !important;
                }
                button {
                    height: 20px;
                    padding: 0 5px;
                }
            }
            .sb-acts {
                float: right;
                width: 25px;
                height: 25px;
                text-align: center;
                line-height: 25px;
                cursor: pointer;
                &:hover {
                    color: #1890ff;
                }
                & > i {
                    font-size: 15px;
                    padding-right: 8px;
                    padding-top: 5px;
                }
            }
        }
    }
}
.and-menu {
    position: absolute;
    background-color: white;
    border: 1px solid #ccc;
    border-bottom-width: 2px;
    border-right-width: 2px;
    z-index: 10;
    display: none;
    &.block {
        display: block;
    }
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
