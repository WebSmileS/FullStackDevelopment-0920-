<template>
    <div class="scoped scoped-white table-select-center">
        <a-button
            v-if="editAble"
            type="primary"
            class="yrt-roles-list-add"
            :icon="lockState"
            @click="editLockChange"
        ></a-button>
        <a-button
            v-if="editAble"
            type="primary"
            class="yrt-roles-list-add"
            icon="plus"
            :disabled="locked || newRecordFlag"
            @click="insertRole"
        ></a-button>

        <!-- 角色列表开始 -->
        <vxe-table
            border="none"
            height="100%"
            show-overflow
            highlight-hover-row
            highlight-current-row
            ref="rolesTable"
            :loading="loading"
            :edit-config="{ trigger: 'click', mode: 'cell', activeMethod: allowEdit }"
            @current-change="roleSelectedChange"
            @edit-closed="editClosedEvent"
            :data="roles"
        >
            <vxe-table-column field="name" title="角色列表" :edit-render="{ name: 'input', attrs: { type: 'text' } }">
            </vxe-table-column>
            <vxe-table-column width="35" v-if="editAble">
                <template v-slot="{ row }">
                    <div v-if="row.editAble">
                        <a-icon
                            @click="delRole(row)"
                            :disabled="locked"
                            type="delete"
                            theme="twoTone"
                            :twoToneColor="deleteColor"
                        >
                        </a-icon>
                    </div>
                </template>
            </vxe-table-column>
        </vxe-table>
        <!-- 角色列表结束 -->
    </div>
</template>
<script>
export default {
    name: 'roleList',
    props: {
        roles: [], // 角色列表，ex: {id:<string>, name:<string>, editAble:<bool>}
        editAble: Boolean, // 是否允许内部编辑
        loading: Boolean, // 加载状态
        rowChangeAble: Boolean, // 能否切换选择的行，用于角色树未保存不允许切换
        rowChangeWarn: String // 当不允许切换时，弹出的警告信息
    },
    data() {
        return {
            selectRoleFlag: false, // 选中角色标记
            selectRole: {}, // 当前选中角色
            locked: true, // 是否锁定编辑
            newRecordFlag: false, // 刚创建新行，还未提交
            currentRow: {} // 当前选中行，用于回退
        };
    },
    methods: {
        init() {
            this.roels = [];
            this.selectRole = {};
            this.loading = false;
            this.selectRoleFlag = false;
            this.newRecordFlag = false;
            this.locked = true;
            this.rowChangeAble = true;
        },
        setSelectedRole(role) {
            this.$refs.rolesTable.setCurrentRow(role);
            this.roleSelectedChange({ row: role });
        },
        clearSelected() {
            this.$refs.rolesTable.clearCurrentRow();
        },
        roleSelectedChange({ row }) {
            if (this.rowChangeAble) {
                this.currentRow = row;
                this.$emit('selectChange', row);
            } else {
                this.$refs.rolesTable.setCurrentRow(this.currentRow);
                if (this.rowChangeWarn.length > 0) {
                    this.$message.warning(this.rowChangeWarn);
                }
            }
        },
        editLockChange() {
            this.locked = !this.locked;
        },
        allowEdit({ row }) {
            // 系统内置角色不能修改
            if (!row.editAble) {
                return false;
            }

            return !this.locked;
        },
        delRole(row) {
            if (this.locked || !row.editAble) {
                return;
            }

            // 如果为新建记录，还未提交的
            if (row.id.length <= 0 || row.id === '') {
                this.cancelAdd();
                return;
            }
            // 弹框确认是否删除
            const _this = this;
            _this.$confirm({
                title: '删除',
                content: '确认删除该角色？',
                centered: true,
                maskClosable: false,
                onOk() {
                    // 上报删除
                    _this.$emit('deleteRole', row.id);
                },
                onCancel() {}
            });
        },
        editClosedEvent({ row, column }) {
            let table = this.$refs.rolesTable;
            let field = column.property;
            if (table.isUpdateByRow(row, field)) {
                this.loading = true;
                if (!row.id || row.id.lenght <= 0) {
                    // 上报增加
                    this.$emit('addRole', row);
                } else {
                    // 上报修改
                    this.$emit('updateRole', row);
                }
            }
        },
        confirmAdd(roleID) {
            if (this.roles || this.roles[0].id.length === 0) {
                this.roles[0].id = roleID;
                this.newRecordFlag = false;
                this.$refs.rolesTable.syncData();
            }
            this.loading = false;
        },
        cancelAdd() {
            for (let i = 0; i < this.roles.length; i++) {
                if (this.roles[0].id.length === 0) {
                    this.roles.splice(i, 1);
                    this.newRecordFlag = false;
                    this.$refs.rolesTable.syncData();
                    break;
                }
            }
            this.loading = false;
        },
        confirmDel(id) {
            for (let i = 0; i < this.roles.length; i++) {
                if (this.roles[i].id === id) {
                    this.roles.splice(i, 1);
                    this.$refs.rolesTable.syncData();
                    break;
                }
            }
            this.loading = false;
        },
        cancelDel() {
            this.loading = false;
        },
        confirmUpdate(row) {
            this.$refs.rolesTable.reloadRow(row, null, 'name');
            this.$refs.rolesTable.syncData();
            this.loading = false;
        },
        cancelUpdate(row) {
            this.$refs.rolesTable.revertData(row);
            this.$refs.rolesTable.syncData();
            this.loading = false;
        },
        directAdd(role) {
            this.roles.unshift(role);
            this.$refs.rolesTable.syncData();
        },
        directDel(roleID) {
            for (let i = 0; i < this.roles.length; i++) {
                if (this.roles[i].id === roleID) {
                    this.roles.splice(i, 1);
                    this.$refs.rolesTable.syncData();
                    break;
                }
            }
        },
        directUpdate(roleID, roleName) {
            for (let i = 0; i < this.roles.length; i++) {
                if (this.roles[i].id === roleID) {
                    this.roles[i].name = roleName;
                    this.$refs.rolesTable.syncData();
                    break;
                }
            }
        },
        async insertRole() {
            if (this.newRecordFlag) {
                return;
            }
            let record = {
                id: '',
                name: '',
                editAble: true
            };
            let table = this.$refs.rolesTable;
            let { row: newRow } = await table.insertAt(record);
            await table.setActiveCell(newRow, 'name');
            this.roles.unshift(newRow);
            table.syncData();
            this.newRecordFlag = true;
        }
    },
    computed: {
        lockState() {
            if (this.locked) {
                return 'lock';
            }
            return 'unlock';
        },
        deleteColor() {
            if (this.locked) {
                return '#d9d9d9';
            }
            return '#eb2f96';
        }
    },
    mounted() {
        this.init();
    }
};
</script>
<style lang="less">
.yrt-roles-list-add {
    float: right;
    z-index: 100;
    top: 10px;
    margin-right: 5px;
}
</style>
