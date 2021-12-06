<template>
    <div class="scoped scoped-white page-initial-inventory">
        <div class="search-form-wrap">
            <a-row class="btn-top-wrap">
                <a-col :span="12">
                    <!-- 按钮区域 -->
                    <ul class="btn-list-wrap">
                        <li>
                            <a-button
                                type="primary"
                                size="small"
                                :disabled="currentEditRow && !currentEditRow.id"
                                @click="handleAddProduct"
                            >
                                新增产品
                                <a-icon type="plus" />
                            </a-button>
                            <a-upload
                                :accept="uploadAccept"
                                name="file"
                                :showUploadList="false"
                                action="/v1/business/initial/importData"
                                @change="handleFileChange"
                                style="display: inline-block"
                            >
                                <a-button type="primary" size="small" :loading="uploading" :disabled="uploading">
                                    导入
                                </a-button>
                            </a-upload>
                            <a-button type="primary" size="small">
                                <a href="/v1/business/initial/export" download="期初库存模板">下载模板</a>
                            </a-button>
                        </li>
                        <li>
                            <a-button @click="handleCreateAccount" type="primary" size="small"> 建账 </a-button>
                            <a-button @click="handleDelAll" type="danger" size="small"> 删除全部 </a-button>
                        </li>
                    </ul>
                </a-col>
                <a-col :span="12">
                    <!-- 普通搜索 -->
                    <div class="input-search-wrap">
                        <a-form v-if="isCollapse" :form="searchForm" class="search" style="height: 24px">
                            <a-form-item style="line-height: 24px">
                                <a-input-search
                                    v-decorator="['product_name']"
                                    placeholder="产品名称"
                                    size="small"
                                    enterButton
                                    autocomplete="off"
                                    @search="handleSearch"
                                />
                            </a-form-item>
                        </a-form>
                        <div @click="isCollapse = !isCollapse" class="hight-search-text">
                            <a-tooltip placement="left" title="高级搜索">
                                <a-icon :type="isCollapse ? 'down-circle' : 'up-circle'" />
                            </a-tooltip>
                        </div>
                    </div>
                </a-col>
            </a-row>
            <a-form
                v-show="!isCollapse"
                class="search-form"
                layout="horizontal"
                :colon="false"
                :form="searchForm"
                :label-col="{ span: 6 }"
                :wrapper-col="{ span: 18 }"
            >
                <a-row class="hight-search-wrap">
                    <a-col :span="5">
                        <a-form-item label="仓库">
                            <a-tree-select
                                v-decorator="['warehouse_id']"
                                treeNodeFilterProp="title"
                                showSearch
                                :style="{ marginTop: '4px' }"
                                :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                :treeData="warehouseList"
                                placeholder="请选择"
                                searchPlaceholder=""
                                treeDefaultExpandAll
                                allowClear
                            >
                            </a-tree-select>
                        </a-form-item>
                    </a-col>
                    <a-col :span="5">
                        <a-form-item label="产品名称">
                            <a-input
                                allowClear
                                v-decorator="['product_name']"
                                placeholder="请输入要查询的名称"
                                autocomplete="off"
                            />
                        </a-form-item>
                    </a-col>
                    <a-col :span="5">
                        <a-form-item label="厂商名称">
                            <a-input
                                allowClear
                                v-decorator="['vendor_name']"
                                placeholder="请输入要查询的名称"
                                autocomplete="off"
                            />
                        </a-form-item>
                    </a-col>
                    <a-col :span="5">
                        <a-form-item label="状态">
                            <a-select v-decorator="['status']" placeholder="请选择状态">
                                <a-select-option value="-1"> 全部 </a-select-option>
                                <a-select-option value="0"> 未建账 </a-select-option>
                                <a-select-option value="1"> 已建账 </a-select-option>
                            </a-select>
                        </a-form-item>
                    </a-col>
                    <a-col :span="4">
                        <a-button @click="handleSearch" type="primary"> 搜索 </a-button>
                    </a-col>
                </a-row>
            </a-form>
        </div>
        <div style="margin-top: 5px; background: #fff">
            <vxe-grid
                ref="xGrid"
                align="center"
                :height="scrollYNumber"
                v-bind="gridOptions"
                :data="dataSource"
                @page-change="handlePageChange"
            >
                <vxe-table-column field="index" title="NO." width="50px" :resizable="false" fixed="left">
                    <template v-slot="{ row, seq }">
                        <div>
                            <i :class="!row.id ? 'red-dot-add' : 'hide'"></i>
                            <span>{{ seq }}</span>
                        </div>
                    </template>
                </vxe-table-column>
                <vxe-table-column field="warehouse_id" title="仓库" :resizable="false">
                    <template v-slot="{ row }">
                        <a-tree-select
                            v-if="checkRowEditable(row)"
                            v-model="row.warehouse_id"
                            treeNodeFilterProp="title"
                            showSearch
                            :style="{ marginTop: '4px' }"
                            :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                            :treeData="warehouseList"
                            placeholder="请选择"
                            searchPlaceholder=""
                            treeDefaultExpandAll
                            allowClear
                        >
                        </a-tree-select>
                        <span v-else>{{ getWarehouseNameById(row.warehouse_id) }}</span>
                    </template>
                </vxe-table-column>
                <vxe-table-column field="vendor_name" title="厂商名称" :resizable="false" />
                <vxe-table-column field="product_name" title="产品名称" :resizable="false" />
                <vxe-table-column field="product_spec" title="规格型号" :resizable="false" />
                <vxe-table-column field="batch_number" title="批号" width="100px" :resizable="false">
                    <template v-slot="{ row }">
                        <a-input v-if="checkRowEditable(row)" v-model="row.batch_number" />
                        <span v-else>{{ row.batch_number }}</span>
                    </template>
                </vxe-table-column>
                <vxe-table-column field="produce_date" title="生产日期" width="120px" :resizable="false">
                    <template v-slot="{ row }">
                        <a-date-picker
                            v-if="checkRowEditable(row)"
                            v-model="row.produce_date"
                            format="YYYY-MM-DD"
                            placeholder=""
                            allowClear
                            class="input"
                            style="width: 100%"
                        />
                        <span v-else>{{ row.produce_date | formatDate }}</span>
                    </template>
                </vxe-table-column>
                <vxe-table-column field="overdue_date" title="失效日期" width="120px" :resizable="false">
                    <template v-slot="{ row }">
                        <a-date-picker
                            v-if="checkRowEditable(row)"
                            v-model="row.overdue_date"
                            format="YYYY-MM-DD"
                            placeholder=""
                            allowClear
                            class="input"
                            style="width: 100%"
                        />
                        <span v-else>{{ row.overdue_date | formatDate }}</span>
                    </template>
                </vxe-table-column>
                <vxe-table-column field="unit" title="单位" :resizable="false" width="80px">
                    <template v-slot="{ row }">
                        <a-cascader
                            v-if="checkRowEditable(row)"
                            :allowClear="false"
                            :options="row.unitList"
                            v-model="row.product_unit_id"
                            changeOnSelect
                            :displayRender="displayUnitRender"
                            @click="handleGetUnitList(row)"
                            @change="handleUnitChange"
                            placeholder=""
                            style="width: 100%"
                        />
                        <span v-else>{{ row.product_unit }}</span>
                    </template>
                </vxe-table-column>
                <vxe-table-column field="quantity" title="数量" width="80px">
                    <template v-slot="{ row }">
                        <a-input-number v-if="checkRowEditable(row)" :min="0" v-model="row.quantity" />
                        <span v-else>{{ row.quantity }}</span>
                    </template>
                </vxe-table-column>
                <vxe-table-column field="unit_price" title="单价" width="120px" :resizable="true">
                    <template v-slot="{ row }">
                        <a-input-number
                            v-if="checkRowEditable(row)"
                            :min="0"
                            style="width: 100%"
                            v-model="row.unit_price"
                        />
                        <span v-else>{{ row.unit_price }}</span>
                    </template>
                </vxe-table-column>
                <vxe-table-column field="status" title="状态" width="100px" align="center" :resizable="false">
                    <template v-slot="{ row }">
                        <template v-if="row.status === 0">
                            <a-tag color="#2db7f5">未建账</a-tag>
                        </template>
                        <template v-else>
                            <a-tag color="#87d068">已建账</a-tag>
                        </template>
                    </template>
                </vxe-table-column>
                <vxe-table-column field="operate" title="操作" width="14%" align="center">
                    <template v-slot="{ row }">
                        <template v-if="row.status === 0">
                            <template>
                                <template v-if="checkRowEditable(row)">
                                    <a-button type="primary" @click="handleSaveRow(row)">保存</a-button>
                                    <a-button v-if="row.id" @click="handleCancelRow(row)">取消</a-button>
                                </template>
                                <template v-else>
                                    <a-button @click="handleEditRow(row)" ghost type="primary">编辑</a-button>
                                </template>
                                <a-popconfirm
                                    title="确定要删除该条产品吗？"
                                    ok-text="是"
                                    cancel-text="否"
                                    @confirm="handleDelRow(row)"
                                >
                                    <a-button>删除</a-button>
                                </a-popconfirm>
                            </template>
                        </template>
                    </template>
                </vxe-table-column>
                <template #pager>
                    <vxe-pager
                        :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                        :current-page="gridOptions.pagerConfig.currentPage"
                        :page-size="gridOptions.pagerConfig.pageSize"
                        :total="gridOptions.pagerConfig.total"
                        @page-change="handlePageChange"
                    >
                    </vxe-pager>
                </template>
            </vxe-grid>
        </div>
        <add-product-modal
            :visible="addProductModalVisible"
            @on-cancel="handleCancelAddProduct"
            @on-return="handleProductReturn"
        />
        <task-result-modal
            :visible="taskResultModalVisible"
            :task="task"
            :errorExcel="errorExcel"
            @on-done="handleTaskDone"
        />
    </div>
</template>

<script>
import moment from 'moment';
import {
    saveInitInventoryAPI,
    platformUnitTree,
    platformUnitParents,
    delInitialInventoryAPI,
    dealerUnitTreeAPI,
    createInitialInventoryAPI,
    searchInitInventoryListAPI,
    searchImportInitInventoryResultAPI,
    searchCreateInitInventoryResultAPI,
    delAllInitialInventoryAPI,
    warehouseUsableTopListAPI
} from '@/service/pageAjax';
import { ExcelAccept } from '@/config/constants';
import addProductModal from './modal/addProductModal';
import taskResultModal from './modal/taskResultModal';

const TaskDelay = 5000; // 轮训任务间隔时间

export default {
    data() {
        return {
            searchForm: this.$form.createForm(this, { name: 'search_form' }), // 查询表单
            currentEditRow: undefined, // 当前编辑行
            uploading: false, // 是否正在上传
            editing: false, // 是否正在编辑
            isCollapse: true,
            progress: 0, // 上传进度
            gridOptions: {
                border: true,
                resizable: true,
                keepSource: true,
                showOverflow: true,
                height: 530,
                loading: false,
                pagerConfig: {
                    total: 0,
                    currentPage: 1,
                    pageSize: 10,
                    pageSizes: [10, 20, 30, 40]
                },
                editConfig: {
                    trigger: 'manual',
                    mode: 'row',
                    showStatus: true,
                    icon: 'fa fa-file-text-o'
                }
            },
            dataSource: [], // 列表数据源
            originalDataSource: [], // 缓存原始数据源，用于判断表格行数据初始状态
            addProductModalVisible: false,
            taskResultModalVisible: false,
            task: {
                percent: 0, // 任务进度
                status: 'active', // 任务状态 uploading(excel文件上传中) | active(后台任务进行中) | exception(发生错误) | success(任务成功)
                errMessage: '', // 任务错误信息
                id: undefined, // 任务id
                type: undefined, // 任务类型 createAccount（建账进度） | import（后台导入进度）
                pollTimer: null // 任务轮训timer
            },
            orgId: this.$cookie.get('userbelong'),
            uploadAccept: ExcelAccept, // 文件上传格式限制
            errorExcel: {
                url: undefined, // 包含错误的excel文件下载地址
                filename: undefined // 文件名
            },
            warehouseList: []
        };
    },
    computed: {
        scrollYNumber() {
            // 固定表格的头部
            return window.innerHeight - (this.isCollapse ? 111 : 204);
        }
    },
    filters: {
        formatDate(date) {
            if (date && typeof date === 'object') {
                return date.format('YYYY-MM-DD');
            }
            return date;
        }
    },
    methods: {
        // 初始化
        async init() {
            const taskType = localStorage.getItem('taskType');
            const taskId = localStorage.getItem('taskId');
            const taskPercent = localStorage.getItem('taskPercent');
            this.task.id = taskId;
            this.task.type = taskType;
            this.searchForm.setFieldsValue({
                status: '-1'
            });
            await this.getWarehouseTopList();
            if (taskId) {
                this.task.percent = Number(taskPercent);
                this.startPollTask();
                this.$Utils.globalDragCenterFn('import-progress-modal');
                this.taskResultModalVisible = true;
            } else {
                this.clearPollTask();
                this.handleSearch();
            }
        },

        // 搜索
        async handleSearch() {
            if (this.gridOptions.loading) {
                return;
            }
            this.editing = false;
            this.currentEditRow = undefined;
            this.gridOptions.loading = true;
            const fieldsValue = this.searchForm.getFieldsValue();
            const pageNum = this.gridOptions.pagerConfig.currentPage;
            const pageSize = this.gridOptions.pagerConfig.pageSize;
            const params = {
                ...fieldsValue,
                status: fieldsValue.status ? Number(fieldsValue.status) : -1,
                org_id: this.orgId
            };
            const res = await searchInitInventoryListAPI(params, pageNum, pageSize);
            this.dataSource = res.rows || [];
            this.originalDataSource = JSON.parse(JSON.stringify(res.rows || []));
            this.gridOptions.pagerConfig.total = res.total;
            this.gridOptions.loading = false;
        },

        // 添加产品
        handleAddProduct() {
            this.$Utils.globalDragCenterFn('add_product');
            this.addProductModalVisible = true;
        },

        // 分页
        handlePageChange({ currentPage, pageSize }) {
            this.gridOptions.pagerConfig.currentPage = currentPage;
            this.gridOptions.pagerConfig.pageSize = pageSize;
            this.handleSearch();
        },

        // 编辑行
        async handleEditRow(row) {
            if (this.currentEditRow && this.editing) {
                this.$message.warning('请先保存正在编辑的产品');
                return;
            }
            row.overdue_date = row.overdue_date ? moment(row.overdue_date) : undefined;
            row.produce_date = row.produce_date ? moment(row.produce_date) : undefined;
            this.editing = true;
            this.currentEditRow = row;
            await this.handleGetUnitList(row);
            await this.getDefaultUnitOptions(row);
        },

        // 保存行
        async handleSaveRow(row) {
            if (!this.validateRowFields(row)) {
                return;
            }
            await saveInitInventoryAPI({
                batch_number: row.batch_number,
                id: row.id,
                sn: row.sn,
                org_id: row.org_id || this.orgId,
                overdue_date:
                    typeof row.overdue_date === 'object' && row.overdue_date !== null
                        ? row.overdue_date.format('YYYY-MM-DD')
                        : row.overdue_date,
                produce_date:
                    typeof row.produce_date === 'object' && row.produce_date !== null
                        ? row.produce_date.format('YYYY-MM-DD')
                        : row.produce_date,
                product_id: row.product_inner_sn || row.product_id,
                product_name: row.product_name,
                product_spec: row.specification || row.product_spec,
                product_spec_id: row.specification_inner_sn || row.product_spec_id,
                product_type: row.product_type,
                product_unit: row.product_unit,
                product_unit_id: Array.isArray(row.product_unit_id)
                    ? row.product_unit_id[row.product_unit_id.length - 1]
                    : row.product_unit_id,
                quantity: typeof row.quantity === 'number' ? row.quantity.toFixed(4) : row.quantity,
                unit_price: typeof row.unit_price === 'number' ? row.unit_price.toFixed(4) : row.unit_price,
                vendor_id: row.vendor_inner_sn || row.vendor_id,
                vendor_name: row.vendor_name || row.vendor_name,
                warehouse_id: row.warehouse_id,
                warehouse_name: this.getWarehouseNameById(row.warehouse_id)
            });
            this.$message.success('保存成功');
            this.$refs.xGrid.updateData();
            this.editing = false;
            this.currentEditRow = undefined;
            this.handleSearch();
        },

        // 撤销编辑操作
        handleCancelRow(row) {
            const originalItem = this.originalDataSource.find((item) => item.id === row.id);
            if (originalItem) {
                Object.assign(row, originalItem);
            }
            this.editing = false;
            this.currentEditRow = undefined;
        },

        // 删除行
        async handleDelRow(row) {
            if (row.id) {
                await delInitialInventoryAPI(row.id);
                this.handleSearch();
                this.$message.success('删除成功');
            } else {
                this.dataSource.shift();
            }
            this.editing = false;
            this.currentEditRow = undefined;
        },

        // 添加完产品
        async handleProductReturn(product) {
            const newProduct = {
                ...product,
                status: 0,
                product_unit_id: product.unit_inner_sn,
                product_unit: product.unit_name,
                product_spec: product.specification,
                produce_date: product.produce_date ? product.produce_date : undefined,
                overdue_date: product.overdue_date ? product.overdue_date : undefined
            };
            delete newProduct.id;
            this.currentEditRow = newProduct;
            this.editing = true;
            this.dataSource.unshift(newProduct);
            this.addProductModalVisible = false;
            await this.handleGetUnitList(newProduct);
            await this.getDefaultUnitOptions(newProduct);
        },

        // 建账
        handleCreateAccount() {
            this.$confirm({
                title: '期初建账不可逆，且只能执行一次，确定要继续吗？',
                onOk: async () => {
                    const { code, taskId } = await createInitialInventoryAPI(this.orgId);
                    if (code === 0) {
                        this.$message.success('期初建账进行中...');
                        this.task.type = 'createAccount';
                        this.task.id = taskId;
                        localStorage.setItem('taskId', taskId);
                        localStorage.setItem('taskType', 'createAccount');
                        this.$Utils.globalDragCenterFn('import-progress-modal');
                        this.taskResultModalVisible = true;
                        this.startPollTask();
                    }
                }
            });
        },

        // 删除全部
        handleDelAll() {
            this.$confirm({
                title: '确定要删除全部吗？',
                onOk: async () => {
                    await delAllInitialInventoryAPI();
                    this.$message.success('删除全部成功');
                    this.handleSearch();
                }
            });
        },

        handleCancelAddProduct() {
            this.addProductModalVisible = false;
        },

        // 获取单位级联列表
        async handleGetUnitList(row) {
            if (row.isLoadingUnit) {
                return;
            }
            const service = row.product_type === 1 ? platformUnitTree : dealerUnitTreeAPI;
            const res = await service(
                row.vendor_inner_sn || row.vendor_id,
                row.product_inner_sn || row.product_id,
                row.specification_inner_sn || row.product_spec_id
            );
            const circle = (items, prevMeasure, parentName) => {
                // 递归遍历
                items.forEach((item) => {
                    item.label = `${item.name}(${item.measure}×${parentName})`;
                    item.value = item.unit_inner_sn;
                    item.product_unit = `${item.name}(${item.measure}×${parentName})`;
                    item.product_unit_id = item.unit_inner_sn;
                    item.min_measure = item.measure * prevMeasure;
                    if (item.children && item.children.length) {
                        circle(item.children, item.min_measure, item.name);
                    } else {
                        item.children = null;
                    }
                });
            };
            res.tree.forEach((item) => {
                item.label = item.name;
                item.value = item.unit_inner_sn;
                item.product_unit = item.name;
                item.product_unit_id = item.unit_inner_sn;
                item.min_measure = item.measure * 1;
                if (item.children && item.children.length) {
                    circle(item.children, item.min_measure, item.name);
                } else {
                    item.children = null;
                }
            });
            if (res.tree.length) {
                row.unitList = res.tree;
                row.isLoadingUnit = true;
                this.$refs.xGrid.reloadRow(row, row, 'unit');
            }
        },

        // 单位change
        handleUnitChange(_, selectedOptions) {
            this.currentEditRow.product_unit = selectedOptions[selectedOptions.length - 1].name;
        },

        // 文件导入change
        handleFileChange({ file }) {
            const { response = {}, status } = file;
            this.uploading = status === 'uploading';
            switch (status) {
                case 'uploading': // 上传中
                    this.task.type = 'import';
                    localStorage.setItem('taskType', 'import');
                    this.task.status = 'uploading';
                    break;
                case 'done': // 上传完成（可能失败）
                    if (response.code === 0) {
                        this.$Utils.globalDragCenterFn('import-progress-modal');
                        this.taskResultModalVisible = true;
                        localStorage.setItem('taskId', response.taskId);
                        localStorage.setItem('taskType', 'import');
                        this.task.id = response.taskId;
                        this.task.type = 'import';
                        this.task.errMessage = '';
                        this.task.status = 'active';
                        this.$message.success('文件上传成功，开始导入...');
                        this.startPollTask();
                    } else {
                        // 非期初建账模板或未知错误
                        this.task.errMessage = response.msg;
                        this.task.status = 'exception';
                        this.$message.error(response.msg);
                        this.clearPollTask();
                    }
                    break;
                case 'error': // 上传错误
                    this.$message.error(response.message || '文件上传错误');
                    this.clearPollTask();
                    break;
                default:
                    break;
            }
        },

        handleTaskDone() {
            this.taskResultModalVisible = false;
            this.handleSearch();
            this.errorExcel = {
                url: undefined,
                filename: undefined
            };
            this.clearPollTask();
        },

        // 检查当前行是否可编辑
        checkRowEditable(row) {
            return this.editing && this.currentEditRow && this.currentEditRow.id === row.id;
        },

        // 保存时，对行数据进行校验
        validateRowFields(row) {
            const {
                quantity,
                unit_price: unitPrice,
                warehouse_id: warehouseId,
                product_unit_id: productUnitId,
                produce_date: produceDate,
                overdue_date: overdueData
            } = row;

            if (unitPrice < 0) {
                this.$message.error('单价不能小于0');
                return false;
            }

            if (!warehouseId) {
                this.$message.error('仓库不能为空');
                return false;
            }

            if (!quantity) {
                this.$message.error('数量不能为空');
                return false;
            }

            if (quantity < 0) {
                this.$message.error('数量不能小于0');
                return false;
            }

            if (!productUnitId) {
                this.$message.error('单位不能为空');
                return false;
            }

            if (produceDate && moment(overdueData).isBefore(moment(produceDate))) {
                this.$message.error('失效日期不能早于生产日期');
                return false;
            }

            return true;
        },

        displayUnitRender({ labels }) {
            const label = labels[labels.length - 1];
            return label ? label.split('(')[0] : '';
        },

        getWarehouseNameById(id) {
            const warehouse = this.warehouseList.find((item) => item.value === id);
            if (warehouse) {
                return warehouse.title;
            }
        },

        async getDefaultUnitOptions(row) {
            const { info } = await platformUnitParents(
                row.vendor_id || row.vendor_inner_sn,
                row.product_id || row.product_inner_sn,
                row.product_spec_id || row.specification_inner_sn,
                row.product_unit_id
            );
            row.product_unit_id = [];
            row.unit_inner_sn = [];
            const generateUnitList = (item) => {
                row.unit_inner_sn.unshift(item.unit_inner_sn);
                row.product_unit_id.unshift(item.unit_inner_sn);
                if (item.parent) {
                    generateUnitList(item.parent);
                }
            };
            generateUnitList(info);
        },

        // 获取一级仓库列表
        async getWarehouseTopList() {
            try {
                const { code, list = [], msg } = await warehouseUsableTopListAPI();
                if (code === 0) {
                    this.warehouseList = list.map((item) => ({
                        ...item,
                        title: item.name,
                        value: item.warehouse_model_inner_sn,
                        key: item.warehouse_model_inner_sn
                    }));
                } else {
                    this.$message.error(msg);
                }
            } catch (error) {
                console.error(error);
            }
        },

        /**
         * 开启轮询任务
         */
        startPollTask() {
            const pollTask = async () => {
                try {
                    const service =
                        this.task.type === 'import'
                            ? searchImportInitInventoryResultAPI
                            : searchCreateInitInventoryResultAPI;
                    const { code, msg } = await service(this.task.id);
                    switch (code) {
                        case 0: // 任务成功
                            this.task.errMessage = msg;
                            this.clearPollTask(0);
                            break;
                        case 1: // 任务不存在
                        case 500: // 错误的模板数据
                            this.taskResultModalVisible = false;
                            this.clearPollTask();
                            this.$message.error(msg);
                            this.task.errMessage = msg;
                            this.task.status = 'exception';
                            break;
                        case 2: // 进行中
                            this.task.errMessage = `${this.task.type === 'import' ? '导入' : '建账'}任务正在进行中`;
                            this.task.status = 'active';
                            // 前端mock的进度条，优化体验
                            if (this.task.percent >= 80) {
                                this.task.percent = 99;
                            } else {
                                this.task.percent += 20;
                            }
                            localStorage.setItem('taskPercent', this.task.percent);
                            break;
                        case 3: // 任务完成，但包含错误数据，需要触发错误文件的下载
                            this.clearPollTask(3);
                            this.errorExcel.url = `/v1/comm/download?delete=true&fileName=${msg}`;
                            this.errorExcel.filename = msg;
                            break;
                        default:
                            break;
                    }
                } catch (err) {
                    this.clearPollTask();
                }
            };

            this.task.pollTimer = setInterval(() => {
                pollTask();
            }, TaskDelay);
        },

        /**
         * 清空操作
         */
        clearPollTask(code) {
            clearInterval(this.task.pollTimer);
            switch (code) {
                case 0: // 任务成功
                    this.task.status = 'success';
                    this.task.percent = 100;
                    this.task.id = undefined;
                    this.task.errMessage = `${this.task.type === 'import' ? '导入成功' : '建账成功'}`;
                    localStorage.removeItem('taskId');
                    break;
                case 3: // 任务完成，但包含错误数据
                    this.task.id = undefined;
                    this.task.status = 'exception';
                    this.task.percent = 100;
                    this.task.errMessage = '文件中包含错误数据，请下载excel文件进行查看';
                    localStorage.removeItem('taskId');
                    break;
                default:
                    this.task = {
                        percent: 0,
                        status: 'active',
                        errMessage: '',
                        id: undefined,
                        type: undefined,
                        pollTimer: null
                    };
                    localStorage.removeItem('taskId');
                    localStorage.removeItem('taskType');
                    localStorage.removeItem('taskPercent');
                    break;
            }
        }
    },
    mounted() {
        this.init();
    },
    components: {
        addProductModal,
        taskResultModal
    }
};
</script>

<style lang="less">
.page-initial-inventory {
    .ant-form-item-control {
        line-height: 24px;
    }
    .ant-form-item-label {
        line-height: 32px !important;
    }
}
</style>
