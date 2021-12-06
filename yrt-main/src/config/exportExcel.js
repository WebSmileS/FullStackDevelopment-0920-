class ExportExcel {
    // 主标题
    getTitle() {
        const title = this.formData.title;
        return title ? `<h2 style="text-align: center;">${this.formData.title}</h2>` : '';
    }

    // 表单组
    getForm() {
        const groups = this.formData.groups || [];
        const marginRight = '&nbsp;&nbsp;';

        return `
        ${groups
            .map((group) => {
                return `<p>
                    ${group
                        .map((item) => {
                            const { label, value } = item;
                            return `<span><b>${label}：</b>${value || '-'}</span>${marginRight}`;
                        })
                        .join('')}
                </p>`;
            })
            .join('')}
        `;
    }

    // 备注
    getDesc() {
        const desc = this.formData.desc;
        return desc ? `<p><b>${desc.label}：</b>${desc.value || '-'}</p>` : '';
    }

    // 表头
    getTableHeader() {
        const columns = this.columns;
        return `<tr>
            ${columns
                .map((column) => {
                    const title = column.title;
                    return `<th>${title}</th>`;
                })
                .join('')}
        </tr>`;
    }

    // 表体
    getTableBody() {
        const columns = this.columns;
        const rows = this.rows;

        return `
            ${rows
                .map((row, index) => {
                    row.index = index + 1; // 设置table数据序号列的值
                    row.idx = index + 1; // 设置table数据序号列的值
                    return `<tr>
                        ${columns
                            .map((column) => {
                                const field = column.dataIndex || column.property; // 表头key
                                if (Object.hasOwnProperty.call(row, field) && (row[field] || row[field] === 0)) {
                                    // 增加\t为了不让表格显示科学计数法或者其他格式
                                    return `<td style="text-align: center">${row[field] + '\t'}</td>`;
                                } else {
                                    return `<td></td>`;
                                }
                            })
                            .join('')}
                    </tr>`;
                })
                .join('')} 
        `;
    }

    // 表尾
    getTableFooter() {
        const footerData = this.footerData;
        const style = 'font-weight: bold; text-align: center;';

        return `<tfoot>${footerData
            .map((foot) => {
                return `<tr>
                    ${foot
                        .map((item, index) => {
                            if (index === 0) {
                                // 合计文本
                                return `<td style="${style}">${item + '\t'}</td>`;
                            } else {
                                return item ? `<td style="${style}">${item + '\t'}</td>` : `<td></td>`;
                            }
                        })
                        .join('')}
                </tr>`;
            })
            .join('')}</tfoot>`;
    }

    getTable() {
        const header = this.getTableHeader();
        const body = this.getTableBody();
        const footer = this.getTableFooter();
        return `<table border="1">${header}${body}${footer}</table>`;
    }

    getHtml() {
        const dataTable = this.getTable();
        const title = this.getTitle();
        const formWrap = this.getForm();
        const desc = this.getDesc();

        return `${title}${formWrap}${dataTable}${desc}`;
    }

    initialize(columns = [], rows = [], footerData = [], formData = {}) {
        let hasAction = false;
        this.columns = columns.filter((column) => {
            const field = column.dataIndex || column.property;
            if (this.hasAction(field) || this.hasAction(column.title)) {
                hasAction = true;
                return false;
            } else {
                return true;
            }
        });

        this.rows = rows;

        if (hasAction) {
            this.footerData = JSON.parse(JSON.stringify(footerData));
            this.footerData.forEach((item) => {
                item.pop();
            }); // 如果导出的表格内容包含操作列 删除表尾最后一项 防止撑开表格
        } else {
            this.footerData = footerData;
        }

        this.formData = formData;
    }

    /**
     * formData:{
     *     title: '销售单' 标题
     *     desc:{
     *         label: '摘要/备注',
     *         value: 'xxx'
     *     },
     *     groups:[ 表单组
     *         [
     *             {
     *                  label: '相关机构',
     *                  value: '天津经销售'
     *             },
     *             {
     *                  label: '销售员',
     *                  value: '张三'
     *             }
     *             ...
     *         ]
     *         ...
     *     ]
     * }
     * @param columns []
     * @param rows []
     * @param footerData 表尾数据 二维数组 [[], ...]
     * @param formData 表单数据
     */
    toExcel(columns, rows, footerData, formData) {
        this.initialize(columns, rows, footerData, formData);
        const html = this.getHtml();
        // Worksheet名
        let worksheet = 'Sheet1';
        let uri = 'data:application/vnd.ms-excel;base64,';

        // 下载的表格模板数据
        let template = `<html xmlns:o="urn:schemas-microsoft-com:office:office" 
        xmlns:x="urn:schemas-microsoft-com:office:excel" 
        xmlns="http://www.w3.org/TR/REC-html40">
        <head><!--[if gte mso 9]><xml><x:ExcelWorkbook><x:ExcelWorksheets><x:ExcelWorksheet>
            <x:Name>${worksheet}</x:Name>
            <x:WorksheetOptions><x:DisplayGridlines/></x:WorksheetOptions></x:ExcelWorksheet>
            </x:ExcelWorksheets></x:ExcelWorkbook></xml><![endif]-->
            <meta charset="UTF-8">
            </head><body>${html}</body></html>`;
        // 下载模板
        window.location.href = uri + base64(template);

        // 输出base64编码
        function base64(s) {
            return window.btoa(unescape(encodeURIComponent(s)));
        }
    }

    // 是否为操作列
    hasAction(value) {
        if (!value) return false;
        const ACTION = 'action';
        const ACTION_TEXT = '操作';
        return value.indexOf(ACTION) !== -1 || value.indexOf(ACTION_TEXT) !== -1;
    }
}

export default new ExportExcel();
