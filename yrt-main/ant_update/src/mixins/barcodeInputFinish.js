export default {
    methods: {
        /**
         * 扫码输入框输入完成时，调取条码解析接口，之后使用解析返回的结果更新行数据
         * 需要在组件内提供loader方法用于解析条码
         * 若要最后一个输入单元格输入完成后自动聚焦到默认的单元格，需要在组件内提供handleActiveDefaultCell方法
         * @param {string} barcode 条码
         * @param {object} params 对应vxe-table的事件params
         * @param {function} loader 解析条码的API接口
         * @param {array} updateProperties 需要使用接口返回信息更新行数据的属性名
         * @param {string} updateProperties[][0] 返回信息中的属性名
         * @param {string} updateProperties[][1] 行数据中对应的属性名
         * @param {string} updateProperties[][2] 该信息的名称
         */
        async _mixinOnBarcodeInputFinish(barcode, { row, rowIndex, column, $table }, loader, updateProperties) {
            const nextRowIndex = rowIndex + 1;
            if (!barcode) {
                $table.setActiveCell($table.$props.data[nextRowIndex], column.property);
                return;
            }
            try {
                this.$set(row, 'barcodeInputLoading', true);
                const info = await loader(barcode);
                const changedPropertyStack = [];
                if (
                    (info.product_inner_sn && info.product_inner_sn !== row.product_inner_sn) ||
                    (info.product_vendor_inner_sn && info.product_vendor_inner_sn !== row.product_vendor_inner_sn) ||
                    (info.specification_inner_sn && info.specification_inner_sn !== row.specification_inner_sn)
                ) {
                    this.$message.warning('所扫条码与当前编辑的记录不是同一产品');
                    $table.setActiveCell(row, column.property);
                    return;
                }
                updateProperties.forEach((property) => {
                    if (info[property[0]]) {
                        row[property[1]] = info[property[0]];
                        if (property[2]) {
                            changedPropertyStack.push(property[2]);
                        }
                    }
                });
                if (changedPropertyStack.length) {
                    this.$message.success(`信息已成功录入：${changedPropertyStack.join('、')}`);
                    if ($table.$props.data[nextRowIndex]) {
                        if ($table.$props.data[nextRowIndex].rowMark === 'inputerRow') {
                            if (this.handleActiveDefaultCell) {
                                $table.scrollTo(0);
                                this.handleActiveDefaultCell();
                            }
                        } else {
                            $table.setActiveCell($table.$props.data[nextRowIndex], column.property);
                        }
                    }
                    return;
                }
                this.$message.warning('无可用信息');
                $table.setActiveCell(row, column.property);
            } finally {
                this.$set(row, 'barcodeInputLoading', false);
            }
        }
    }
};
