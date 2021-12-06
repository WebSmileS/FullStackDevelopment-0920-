/** 设置tab展示类型 **/
class TabTypeCfg {
    AgencyTabSet() {
        let tabList = [
            {
                key: '经销商',
                systemTypes: 'dealer', // 0 平台、 1医院 、 2 经销商、 3 厂商
                sTypeNum: 2,
                Pagination: {
                    loading: false,
                    total: 10,
                    current: 1, // 对应后端的 pageSize
                    pageSize: 10, // 对应后端的 pageNum
                    pageNum: 1,
                    pageSizes: [10, 20, 30, 40]
                },
                Datas: [] // 经销商主体数据
            },
            {
                key: '医院',
                systemTypes: 'hospital', // 0 平台、 1医院 、 2 经销商、 3 厂商
                sTypeNum: 1,
                Pagination: {
                    loading: false,
                    total: 10,
                    current: 1, // 对应后端的 pageSize
                    pageSize: 10, // 对应后端的 pageNum
                    pageNum: 1,
                    pageSizes: [10, 20, 30, 40]
                },
                Datas: [] // 经销商主体数据
            },
            {
                key: '厂商',
                systemTypes: 'vendor', // 0 平台、 1医院 、 2  经销商、 3 厂商
                sTypeNum: 3,
                Pagination: {
                    loading: false,
                    total: 10,
                    current: 1, // 对应后端的 pageSize
                    pageSize: 10, // 对应后端的 pageNum
                    pageNum: 1,
                    pageSizes: [10, 20, 30, 40]
                },
                Datas: [] // 经销商主体数据
            }
        ];
        return tabList;
    }
    AgencyTableTab() {
        let tabList = [
            [
                {
                    title: '名称',
                    dataIndex: 'name',
                    key: 'name',
                    width: '20%',
                    align: 'center'
                },
                {
                    title: '别名',
                    dataIndex: 'alias',
                    key: 'alias',
                    width: '16%',
                    align: 'center'
                },
                {
                    title: '统一社会信用代码',
                    dataIndex: 'social_credit_code',
                    key: 'social_credit_code',
                    width: '15%',
                    align: 'center'
                },
                {
                    title: '电话',
                    dataIndex: 'phone',
                    key: 'phone',
                    width: '12%',
                    align: 'center'
                },
                {
                    title: '邮箱',
                    dataIndex: 'email',
                    key: 'email',
                    width: '12%',
                    align: 'center'
                },
                {
                    title: '注册时间',
                    dataIndex: 'register_time',
                    key: 'register_time',
                    width: '15%',
                    align: 'center'
                },
                {
                    title: '认证状态',
                    dataIndex: 'audit_status_text',
                    key: 'audit_status_text',
                    width: '10%',
                    align: 'center',
                    scopedSlots: { customRender: 'audit_status_text' }
                }
            ],
            [
                {
                    title: '名称',
                    dataIndex: 'name',
                    key: 'name',
                    width: '25%',
                    align: 'center'
                },
                {
                    title: '统一社会信用代码',
                    dataIndex: 'social_credit_code',
                    key: 'social_credit_code',
                    width: '20%',
                    align: 'center'
                },
                {
                    title: '电话',
                    dataIndex: 'phone',
                    key: 'phone',
                    width: '15%',
                    align: 'center'
                },
                {
                    title: '邮箱',
                    dataIndex: 'email',
                    key: 'email',
                    width: '15%',
                    align: 'center'
                },
                {
                    title: '注册时间',
                    dataIndex: 'register_time',
                    key: 'register_time',
                    width: '15%',
                    align: 'center'
                },
                {
                    title: '认证状态',
                    dataIndex: 'audit_status_text',
                    key: 'audit_status_text',
                    width: '10%',
                    align: 'center',
                    scopedSlots: { customRender: 'audit_status_text' }
                }
            ],
            [
                {
                    title: '名称',
                    dataIndex: 'name',
                    key: 'name',
                    width: '25%',
                    align: 'center'
                },
                {
                    title: '统一社会信用代码',
                    dataIndex: 'social_credit_code',
                    key: 'social_credit_code',
                    width: '20%',
                    align: 'center'
                },
                {
                    title: '电话',
                    dataIndex: 'phone',
                    key: 'phone',
                    width: '15%',
                    align: 'center'
                },
                {
                    title: '邮箱',
                    dataIndex: 'email',
                    key: 'email',
                    width: '15%',
                    align: 'center'
                },
                {
                    title: '注册时间',
                    dataIndex: 'register_time',
                    key: 'register_time',
                    width: '15%',
                    align: 'center'
                },
                {
                    title: '认证状态',
                    dataIndex: 'audit_status_text',
                    key: 'audit_status_text',
                    width: '10%',
                    align: 'center',
                    scopedSlots: { customRender: 'audit_status_text' }
                }
            ]
        ];
        return tabList;
    }
    AgencyManageTableTab() {
        let tabList = [
            [
                {
                    title: '名称',
                    dataIndex: 'name',
                    key: 'name',
                    minWidth: '250',
                    align: 'center'
                },
                {
                    title: '统一社会信用代码',
                    dataIndex: 'social_credit_code',
                    key: 'social_credit_code',
                    minWidth: '200',
                    align: 'center'
                },
                {
                    title: '别名',
                    dataIndex: 'alias',
                    key: 'alias',
                    minWidth: '200',
                    align: 'center'
                },
                {
                    title: '电话',
                    dataIndex: 'phone',
                    key: 'phone',
                    width: '120',
                    align: 'center'
                },
                {
                    title: '邮箱',
                    dataIndex: 'email',
                    key: 'email',
                    minWidth: '200',
                    align: 'center'
                },
                {
                    title: '认证人',
                    dataIndex: 'certifie_employee_name',
                    key: 'certifie_employee_name',
                    width: '150',
                    align: 'center'
                },
                {
                    title: '状态',
                    dataIndex: 'audit_status_text',
                    key: 'audit_status_text',
                    width: '100',
                    align: 'center',
                    scopedSlots: { customRender: 'audit_status_text' }
                }
            ],
            [
                {
                    title: '名称',
                    minWidth: '250',
                    dataIndex: 'name',
                    key: 'name',
                    align: 'center'
                },
                {
                    title: '统一社会信用代码',
                    dataIndex: 'social_credit_code',
                    key: 'social_credit_code',
                    minWidth: '200',
                    align: 'center'
                },
                {
                    title: '电话',
                    dataIndex: 'phone',
                    key: 'phone',
                    width: '120',
                    align: 'center'
                },
                {
                    title: '邮箱',
                    dataIndex: 'email',
                    key: 'email',
                    minWidth: '150',
                    align: 'center'
                },
                {
                    title: '认证人',
                    dataIndex: 'certifie_employee_name',
                    key: 'certifie_employee_name',
                    width: '150',
                    align: 'center'
                },
                {
                    title: '状态',
                    dataIndex: 'audit_status_text',
                    key: 'audit_status_text',
                    width: '100',
                    align: 'center',
                    scopedSlots: { customRender: 'audit_status_text' }
                }
            ],
            [
                {
                    title: '名称',
                    minWidth: '250',
                    dataIndex: 'name',
                    key: 'name',
                    align: 'center'
                },
                {
                    title: '统一社会信用代码',
                    dataIndex: 'social_credit_code',
                    key: 'social_credit_code',
                    minWidth: '200',
                    align: 'center'
                },
                {
                    title: '电话',
                    dataIndex: 'phone',
                    key: 'phone',
                    width: '120',
                    align: 'center'
                },
                {
                    title: '邮箱',
                    dataIndex: 'email',
                    key: 'email',
                    minWidth: '150',
                    align: 'center'
                },
                {
                    title: '来自',
                    dataIndex: 'data_source_text',
                    key: 'data_source_text',
                    width: '150',
                    align: 'center'
                },
                {
                    title: '认证人',
                    dataIndex: 'certifie_employee_name',
                    key: 'certifie_employee_name',
                    width: '120',
                    align: 'center'
                },
                {
                    title: '状态',
                    dataIndex: 'audit_status_text',
                    key: 'audit_status_text',
                    width: '100',
                    align: 'center',
                    scopedSlots: { customRender: 'audit_status_text' }
                }
            ]
        ];
        return tabList;
    }
    AgencyDownTab() {
        let tabList = [
            [
                {
                    tab: '营业执照',
                    key: '1',
                    btn: true
                },
                {
                    tab: '医疗机构执业许可证',
                    key: '2',
                    btn: true
                },
                {
                    tab: '授权书',
                    key: '4',
                    btn: false
                },
                {
                    tab: '开户许可证',
                    key: '3',
                    btn: false
                },
                {
                    tab: '其它',
                    key: '0',
                    btn: false
                }
            ],
            [
                {
                    tab: '营业执照',
                    key: '1',
                    btn: true
                },
                {
                    tab: '医疗器械经营许可证',
                    key: '2',
                    btn: true
                },
                {
                    tab: '第二类医疗器械经营备案凭证',
                    key: '3',
                    btn: true
                },
                {
                    tab: '授权书',
                    key: '9',
                    btn: false
                },
                {
                    tab: '企业年度公示信息',
                    key: '4',
                    btn: false
                },
                {
                    tab: '印章印模',
                    key: '5',
                    btn: false
                },
                {
                    tab: '空白销售出库单(随货单)',
                    key: '6',
                    btn: false
                },
                {
                    tab: '质量体系调查表',
                    key: '7',
                    btn: false
                },
                {
                    tab: '合格供货方档案',
                    key: '8',
                    btn: false
                },
                {
                    tab: '其它',
                    key: '0',
                    btn: false
                }
            ],
            [
                {
                    tab: '营业执照',
                    key: '1',
                    btn: true
                },
                {
                    tab: '医疗器械生产许可证',
                    key: '2',
                    btn: true
                },
                {
                    tab: '授权书',
                    key: '8',
                    btn: false
                },
                {
                    tab: '企业年度公示信息',
                    key: '3',
                    btn: false
                },
                {
                    tab: '印章印模',
                    key: '4',
                    btn: false
                },
                {
                    tab: '空白销售出库单(随货单)',
                    key: '5',
                    btn: false
                },
                {
                    tab: '质量体系调查表',
                    key: '6',
                    btn: false
                },
                {
                    tab: '合格供货方档案',
                    key: '7',
                    btn: false
                },
                {
                    tab: '其它',
                    key: '0',
                    btn: false
                }
            ]
        ];
        return tabList;
    }
    licDealerTabSet() {
        let tabList = [
            {
                key: '已签约',
                systemTypes: 'dealer', // 0 平台、 1医院 、 2 经销商、 3 厂商
                sTypeNum: 1,
                Pagination: {
                    loading: true,
                    total: 10,
                    current: 1, // 对应后端的 pageSize
                    pageSize: 10, // 对应后端的 pageNum
                    pageNum: 1,
                    pageSizes: [10, 20, 30, 40]
                },
                Datas: [] // 经销商主体数据
            }
        ];
        return tabList;
    }
    licDealerTableTAb() {
        let tabList = [
            [
                {
                    title: '名称',
                    dataIndex: 'name',
                    key: 'name',
                    minWidth: '28%',
                    align: 'center'
                },
                {
                    title: '统一社会信用代码',
                    dataIndex: 'social_credit_code',
                    key: 'social_credit_code',
                    minWidth: '20%',
                    align: 'center'
                },
                {
                    title: '类型',
                    dataIndex: 'enterpriseType_text',
                    key: 'enterpriseType_text',
                    minWidth: '15%',
                    align: 'center'
                },
                {
                    title: '电话',
                    dataIndex: 'phone',
                    key: 'phone',
                    minWidth: '12%',
                    align: 'center'
                },
                {
                    title: '邮箱',
                    dataIndex: 'email',
                    key: 'email',
                    minWidth: '15%',
                    align: 'center'
                },
                {
                    title: '状态',
                    dataIndex: 'audit_status_text',
                    key: 'audit_status_text',
                    minWidth: '10%',
                    align: 'center',
                    scopedSlots: { customRender: 'audit_status_text' }
                }
            ]
        ];
        return tabList;
    }
    licHospitalTabSet() {
        let tabList = [
            {
                key: '已签约',
                systemTypes: 'hospital', // 0 平台、 1医院 、 2 经销商、 3 厂商
                sTypeNum: 1,
                Pagination: {
                    loading: true,
                    total: 10,
                    current: 1, // 对应后端的 pageSize
                    pageSize: 10, // 对应后端的 pageNum
                    pageNum: 1,
                    pageSizes: [10, 20, 30, 40]
                },
                Datas: [] // 经销商主体数据
            }
        ];
        return tabList;
    }
    licHospitalTableTAb() {
        let tabList = [
            [
                {
                    title: '名称',
                    dataIndex: 'name',
                    key: 'name',
                    minWidth: '23%',
                    align: 'center'
                },
                {
                    title: '别名',
                    dataIndex: 'alias',
                    key: 'alias',
                    minWidth: '15%',
                    align: 'center'
                },
                {
                    title: '统一社会信用代码',
                    dataIndex: 'social_credit_code',
                    key: 'social_credit_code',
                    minWidth: '15%',
                    align: 'center'
                },
                {
                    title: '类型',
                    dataIndex: 'enterpriseType_text',
                    key: 'enterpriseType_text',
                    minWidth: '15%',
                    align: 'center'
                },
                {
                    title: '电话',
                    dataIndex: 'phone',
                    key: 'phone',
                    minWidth: '10%',
                    align: 'center'
                },
                {
                    title: '邮箱',
                    dataIndex: 'email',
                    key: 'email',
                    minWidth: '12%',
                    align: 'center'
                },
                {
                    title: '状态',
                    dataIndex: 'audit_status_text',
                    key: 'audit_status_text',
                    minWidth: '10%',
                    align: 'center',
                    scopedSlots: { customRender: 'audit_status_text' }
                }
            ]
        ];
        return tabList;
    }
    licVendorTabSet() {
        let tabList = [
            {
                key: '已签约',
                systemTypes: 'vendor', // 0 平台、 1医院 、 2 经销商、 3 厂商
                sTypeNum: 1,
                Pagination: {
                    loading: true,
                    total: 10,
                    current: 1, // 对应后端的 pageSize
                    pageSize: 10, // 对应后端的 pageNum
                    pageNum: 1,
                    pageSizes: [10, 20, 30, 40]
                },
                Datas: [] // 经销商主体数据
            }
        ];
        return tabList;
    }
    licVendorTableTAb() {
        let tabList = [
            [
                {
                    title: '名称',
                    dataIndex: 'name',
                    key: 'name',
                    minWidth: '28%',
                    align: 'center'
                },
                {
                    title: '统一社会信用代码',
                    dataIndex: 'social_credit_code',
                    key: 'social_credit_code',
                    minWidth: '20%',
                    align: 'center'
                },
                {
                    title: '类型',
                    dataIndex: 'enterpriseType_text',
                    key: 'enterpriseType_text',
                    minWidth: '15%',
                    align: 'center'
                },
                {
                    title: '电话',
                    dataIndex: 'phone',
                    key: 'phone',
                    minWidth: '12%',
                    align: 'center'
                },
                {
                    title: '邮箱',
                    dataIndex: 'email',
                    key: 'email',
                    minWidth: '15%',
                    align: 'center'
                },
                {
                    title: '状态',
                    dataIndex: 'audit_status_text',
                    key: 'audit_status_text',
                    minWidth: '10%',
                    align: 'center',
                    scopedSlots: { customRender: 'audit_status_text' }
                }
            ]
        ];
        return tabList;
    }
}
export default new TabTypeCfg();
