import Vue from 'vue';
import Router from 'vue-router';
import store from './store';

Vue.use(Router);
const routes = [
    {
        path: '/Login',
        name: 'Login',
        component: () => import('@/views/commonPage/Login'),
        meta: {
            title: '登录',
            requiresAuth: false
        }
    },
    {
        path: '/Regist',
        name: 'Regist',
        component: () => import('@/views/commonPage/Regist'),
        meta: {
            title: '账户注册',
            requiresAuth: true
        }
    },
    {
        path: '/print',
        name: 'print',
        component: () => import('@/views/commonPage/print'),
        meta: {
            title: '打印',
            requiresAuth: true
        }
    },
    {
        path: '/newprint',
        name: 'newprint',
        component: () => import('@/views/commonPage/Print/index'),
        meta: {
            title: '打印',
            requiresAuth: true
        },
        children: [
            {
                path: 'inboundOrder',
                name: 'PrintInboundOrder',
                component: () => import('@/views/commonPage/Print/InboundOrder/index'),
                meta: {
                    title: '入库单打印',
                    requiresAuth: true
                }
            },
            {
                path: 'outboundOrder',
                name: 'PrintOutboundOrder',
                component: () => import('@/views/commonPage/Print/OutboundOrder/index'),
                meta: {
                    title: '出库单打印',
                    requiresAuth: true
                }
            },
            {
                path: 'warehouseTransfer',
                name: 'PrintWarehouseTransfer',
                component: () => import('@/views/commonPage/Print/warehouseTransfer/index'),
                meta: {
                    title: '调拨单打印',
                    requiresAuth: true
                }
            },
            {
                path: 'useRecord',
                name: 'PrintUseRecord',
                component: () => import('@/views/commonPage/Print/useRecord/index'),
                meta: {
                    title: '使用记录打印',
                    requiresAuth: true
                }
            },
            {
                path: 'purchaseOrder',
                name: 'PrintPurchaseOrder',
                component: () => import('@/views/commonPage/Print/PurchaseOrder/index'),
                meta: {
                    title: '采购单打印',
                    requiresAuth: true
                }
            },
            {
                path: 'inspectionOrder',
                name: 'PrintInspectionOrder',
                component: () => import('@/views/commonPage/Print/InspectionOrder/index'),
                meta: {
                    title: '验货单打印',
                    requiresAuth: true
                }
            }
        ]
    },
    {
        path: '/largeScreen',
        name: 'largeScreen',
        component: () => import('@/views/largeScreenPresentation/largeScreenIndex'),
        meta: {
            title: '大屏展示',
            requiresAuth: true
        },
        children: [
            {
                path: 'index',
                name: 'index',
                component: () => import('@/views/largeScreenPresentation/indexPage/indexpage/indexPage'),
                meta: {
                    title: '大屏展示',
                    requiresAuth: true
                }
            },
            {
                path: 'process',
                name: 'process',
                component: () => import('@/views/largeScreenPresentation/indexPage/subPages/process/mainProcess'),
                meta: {
                    title: '机构流程',
                    requiresAuth: true
                }
            },
            {
                path: 'productContractInfo',
                name: 'productContractInfo',
                component: () =>
                    import(
                        '@/views/largeScreenPresentation/indexPage/subPages/productContractInfo/productContractInfo'
                    ),
                meta: {
                    title: '产品交易统计',
                    requiresAuth: true
                }
            },
            {
                path: 'productInfo',
                name: 'productInfo',
                component: () =>
                    import('@/views/largeScreenPresentation/indexPage/subPages/productContractInfo/productInfo'),
                meta: {
                    title: '产品信息',
                    requiresAuth: true
                }
            },
            {
                path: 'productMoneyInfo',
                name: 'productMoneyInfo',
                component: () =>
                    import('@/views/largeScreenPresentation/indexPage/subPages/productContractInfo/productMoneyGrid'),
                meta: {
                    title: '产品金额信息',
                    requiresAuth: true
                }
            },
            {
                path: 'productBatchInfo',
                name: 'productBatchInfo',
                component: () =>
                    import('@/views/largeScreenPresentation/indexPage/subPages/productContractInfo/productBatchGrid'),
                meta: {
                    title: '产品批次信息',
                    requiresAuth: true
                }
            },
            {
                path: 'productDealerInfo',
                name: 'productDealerInfo',
                component: () =>
                    import('@/views/largeScreenPresentation/indexPage/subPages/productContractInfo/productDealerGrid'),
                meta: {
                    title: '产品经销商信息',
                    requiresAuth: true
                }
            },
            {
                path: 'productCelInfo',
                name: 'productCelInfo',
                component: () =>
                    import('@/views/largeScreenPresentation/indexPage/subPages/productContractInfo/productCelGrid'),
                meta: {
                    title: '产品科室信息',
                    requiresAuth: true
                }
            },
            {
                path: 'departmentInfo',
                name: 'departmentInfo',
                component: () =>
                    import('@/views/largeScreenPresentation/indexPage/subPages/departmentInfo/departmentWarningInfo'),
                meta: {
                    title: '机构信息',
                    requiresAuth: true
                }
            },
            {
                path: 'departmentInfoDetail',
                name: 'departmentInfoDetail',
                component: () =>
                    import('@/views/largeScreenPresentation/indexPage/subPages/departmentInfo/departmentInfo'),
                meta: {
                    title: '机构信息详情',
                    requiresAuth: true
                }
            },
            {
                path: 'productWarningInfo',
                name: 'productWarningInfo',
                component: () =>
                    import('@/views/largeScreenPresentation/indexPage/subPages/productWarning/productWarningIndex'),
                meta: {
                    title: '产品预警信息',
                    requiresAuth: true
                }
            },
            {
                path: 'productWarningInfoDetail',
                name: 'productWarningInfoDetail',
                component: () =>
                    import('@/views/largeScreenPresentation/indexPage/subPages/productWarning/productWarningInfo'),
                meta: {
                    title: '产品预警信息详情',
                    requiresAuth: true
                }
            }
        ]
    },
    {
        path: '/',
        name: 'home',
        component: () => import('@/views/Home'),
        meta: {
            title: '医润通',
            requiresAuth: true
        },
        children: [
            {
                path: '/ptm',
                name: 'ptm',
                component: () => import('@/views/ptm/PTM'),
                meta: {
                    title: '平台管理相关页面',
                    requiresAuth: true
                },
                children: [
                    {
                        path: 'largeScreenEntry',
                        name: 'largeScreenEntry',
                        component: () => import('@/views/ptm/largeScreenEntry'),
                        meta: {
                            title: '大屏入口',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'agencyManage/RegistAudit',
                        name: 'RegistAudit',
                        component: () => import('@/views/ptm/agencyManage/RegistAudit'),
                        meta: {
                            title: '注册审核',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'agencyManage/AgencyApproval',
                        name: 'AgencyApproval',
                        component: () => import('@/views/ptm/agencyManage/AgencyApproval'),
                        meta: {
                            title: '机构认证',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'agencyManage/AgencyManagement',
                        name: 'AgencyManagement',
                        component: () => import('@/views/ptm/agencyManage/AgencyManagement'),
                        meta: {
                            title: '机构',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'productBuild',
                        name: 'productBuild',
                        component: () => import('@/views/ptm/productManage/productBuild'),
                        meta: {
                            title: '产品初建',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'productNotIssue',
                        name: 'productNotIssue',
                        component: () => import('@/views/ptm/productManage/productBuild'),
                        meta: {
                            title: '未发布产品',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'productIssue',
                        name: 'productIssue',
                        component: () => import('@/views/ptm/productManage/productIssue'),
                        meta: {
                            title: '产品发布',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'productReleased',
                        name: 'productReleased',
                        component: () => import('@/views/ptm/productManage/productReleased'),
                        meta: {
                            title: '产品',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'productCert',
                        name: 'productCert',
                        component: () => import('@/views/ptm/productManage/productCert'),
                        meta: {
                            title: '注册证',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'productIsIssue',
                        name: 'productIsIssue',
                        component: () => import('@/views/ptm/productManage/productReleased'),
                        meta: {
                            title: '已发布产品',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'productSelf',
                        name: 'productSelf',
                        component: () => import('@/views/ptm/productManage/productSelf'),
                        meta: {
                            title: '产品',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'productDealerCheck',
                        name: 'productDealerCheck',
                        component: () => import('@/views/ptm/productManage/productDealerCheck'),
                        meta: {
                            title: '产品送审',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'productAudit',
                        name: 'productAudit',
                        component: () => import('@/views/ptm/productManage/productAudit'),
                        meta: {
                            title: '产品审核',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'operationPlan',
                        name: 'operationPlan',
                        component: () => import('@/views/ptm/productManage/operationPlan'),
                        meta: {
                            title: '手术计划',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'toolsKits',
                        name: 'PTMToolsKits',
                        component: () => import('@/views/ptm/productManage/toolsKits'),
                        meta: {
                            title: '工具包',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'operationKits',
                        name: 'PTMOperationKits',
                        component: () => import('@/views/ptm/productManage/operationKits'),
                        meta: {
                            title: '手术包',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'operationPath',
                        name: 'PTMOperationPath',
                        component: () => import('@/views/ptm/productManage/operationPath'),
                        meta: {
                            title: '手术径路',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'SixtyEightClassification',
                        name: 'SixtyEightClassification',
                        component: () => import('@/views/ptm/productManage/SixtyEightClassification'),
                        meta: {
                            title: '68分类',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'CustomType',
                        name: 'CustomType',
                        component: () => import('@/views/ptm/productManage/CustomType'),
                        meta: {
                            title: '自定义类型',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'OrganizationalStructure',
                        name: 'OrganizationalStructure',
                        component: () => import('@/views/ptm/OrganizationalStructure'),
                        meta: {
                            title: '组织架构',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'HumanResources',
                        name: 'HumanResources',
                        component: () => import('@/views/ptm/HumanResources'),
                        meta: {
                            title: '人力资源',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'userManage/userRoles',
                        name: 'userManage/userRoles',
                        component: () => import('@/views/ptm/userManage/roles'),
                        meta: {
                            title: '角色s',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'userManage/userRole',
                        name: 'userManage/userRole',
                        component: () => import('@/views/ptm/userManage/role'),
                        meta: {
                            title: '角色',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'userManage/user',
                        name: 'userManage/user',
                        component: () => import('@/views/ptm/userManage/user'),
                        meta: {
                            title: '用户',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'maintenance/menuTree',
                        name: 'maintenance/menuTree',
                        component: () => import('@/views/ptm/maintenance/menuTree'),
                        meta: {
                            title: '菜单功能',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'maintenance/motherTree',
                        name: 'maintenance/motherTree',
                        component: () => import('@/views/ptm/maintenance/motherTree'),
                        meta: {
                            title: '菜单母树',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'maintenance/groupTemplate',
                        name: 'maintenance/groupTemplate',
                        component: () => import('@/views/ptm/maintenance/groupTemplate'),
                        meta: {
                            title: '组织模版',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'messageNotifacation',
                        name: 'messageNotifacation',
                        component: () => import('@/views/ptm/messageNotifacation/Index'),
                        meta: {
                            title: '消息通知',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'maintenance/systemRole',
                        name: 'maintenance/systemRole',
                        component: () => import('@/views/ptm/maintenance/systemRole'),
                        meta: {
                            title: '系统角色',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'accountList',
                        name: 'accountList',
                        component: () => import('@/views/ptm/common/accountList'),
                        meta: {
                            title: '帐号初始化查询',
                            requiresAuth: true
                        }
                    }
                ]
            },
            {
                path: '/commonView',
                name: 'commonView',
                component: () => import('@/views/commonPage/commonView'),
                meta: {
                    title: '经销商/厂家/医院相关公用页面',
                    requiresAuth: true
                },
                children: [
                    {
                        path: 'initialInventory',
                        name: 'initialInventory',
                        component: () => import('@/views/commonPage/warehouseManage/initialInventory'),
                        meta: {
                            title: '期初库存',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'inventory',
                        name: 'inventory',
                        component: () => import('@/views/commonPage/warehouseManage/inventory'),
                        meta: {
                            title: '库存',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'godownEntry',
                        name: 'godownEntry',
                        component: () => import('@/views/commonPage/warehouseManage/godownEntry'),
                        meta: {
                            title: '入库',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'outboundOrder',
                        name: 'outboundOrder',
                        component: () => import('@/views/commonPage/warehouseManage/outboundOrder'),
                        meta: {
                            title: '出库',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'buyGrn',
                        name: 'buyGrn',
                        component: () => import('@/views/commonPage/grnAndOdoManage/purchaseGrn'),
                        meta: {
                            title: '采购入库',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'purchaseGrn',
                        name: 'purchaseGrn',
                        component: () => import('@/views/commonPage/grnAndOdoManage/purchaseGrnOrder/index'),
                        meta: {
                            title: '采购入库',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'buyReturn',
                        name: 'buyReturn',
                        component: () => import('@/views/commonPage/grnAndOdoManage/purchaseReturn'),
                        meta: {
                            title: '采购退货',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'saleOdo',
                        name: 'saleOdo',
                        component: () => import('@/views/commonPage/grnAndOdoManage/saleOdo'),
                        meta: {
                            title: '销售出库',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'saleReturn',
                        name: 'saleReturn',
                        component: () => import('@/views/commonPage/grnAndOdoManage/saleReturn'),
                        meta: {
                            title: '销售退货',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'allotOdo',
                        name: 'allotOdo',
                        component: () => import('@/views/commonPage/grnAndOdoManage/allotOdo'),
                        meta: {
                            title: '调拨出库',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'allotGrn',
                        name: 'allotGrn',
                        component: () => import('@/views/commonPage/grnAndOdoManage/allotGrn'),
                        meta: {
                            title: '调拨入库',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'grantOdo',
                        name: 'grantOdo',
                        component: () => import('@/views/commonPage/grnAndOdoManage/grantOdo'),
                        meta: {
                            title: '领用出库',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'giveOdo',
                        name: 'giveOdo',
                        component: () => import('@/views/commonPage/grnAndOdoManage/giveOdo'),
                        meta: {
                            title: '赠送单',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'takeStock',
                        name: 'takeStock',
                        component: () => import('@/views/commonPage/warehouseManage/takeStock'),
                        meta: {
                            title: '盘点',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'warning',
                        name: 'warning',
                        component: () => import('@/views/commonPage/warehouseManage/warning'),
                        meta: {
                            title: '预警',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'report',
                        name: 'report',
                        component: () => import('@/views/commonPage/warehouseManage/report'),
                        meta: {
                            title: '报表',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'warehouseModeling',
                        name: 'warehouseModeling',
                        component: () => import('@/views/commonPage/warehouseManage/warehouseModeling'),
                        meta: {
                            title: '仓库建模',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'warehouseTransfer',
                        name: 'warehouseTransfer',
                        component: () =>
                            import('@/views/commonPage/warehouseManage/warehouseTransfer/warehouseTransfer'),
                        meta: {
                            title: '调拨',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'useRecord',
                        name: 'useRecord',
                        component: () => import('@/views/commonPage/warehouseManage/useRecord/useRecord'),
                        meta: {
                            title: '使用记录',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'orthopedicSurgicalTool',
                        name: 'orthopedicSurgicalTool',
                        component: () => import('@/views/commonPage/warehouseManage/orthopedicSurgicalTool'),
                        meta: {
                            title: '骨科手术工具',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'inboundFinance',
                        name: 'inboundFinance',
                        component: () => import('@/views/commonPage/financeManage/inbound'),
                        meta: {
                            title: '入库查询',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'outboundFinance',
                        name: 'outboundFinance',
                        component: () => import('@/views/commonPage/financeManage/outbound'),
                        meta: {
                            title: '出库查询',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'stockFinance',
                        name: 'stockFinance',
                        component: () => import('@/views/commonPage/financeManage/stock'),
                        meta: {
                            title: '库存查询',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'contractDraft',
                        name: 'contractDraft',
                        component: () => import('@/views/commonPage/contractManage/contractDraft'),
                        meta: {
                            title: '合同草稿',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'contractApprove',
                        name: 'contractApprove',
                        component: () => import('@/views/commonPage/contractManage/contractApprove'),
                        meta: {
                            title: '合同审批',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'contractList',
                        name: 'contractList',
                        component: () => import('@/views/commonPage/contractManage/contractList'),
                        meta: {
                            title: '合同',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'opCooperationDea',
                        name: 'opCooperationDea',
                        component: () => import('@/views/commonPage/contractManage/opCooperationDea'),
                        meta: {
                            title: '骨科合作意向',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'enquiryList',
                        name: 'enquiryList',
                        // component: () => import('@/views/commonPage/buyManage/enquiryList'),
                        // component: () => import('@/views/commonPage/buyManage/enquiryListNew'),
                        component: () => import('@/views/commonPage/buyManage/enquirySheet'),
                        meta: {
                            title: '采购管理',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'plan',
                        name: 'plan',
                        // component: () => import('@/views/commonPage/buyManage/plan'),
                        // component: () => import('@/views/commonPage/buyManage/purchasePlan.vue'),
                        component: () => import('@/views/commonPage/buyManage/planSheet.vue'),
                        meta: {
                            title: '计划',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'purchaseOrder',
                        name: 'purchaseOrder',
                        // component: () => import('@/views/commonPage/buyManage/order'),
                        // component: () => import('@/views/commonPage/buyManage/purchaseOrder.vue'),
                        component: () => import('@/views/commonPage/buyManage/orderSheet.vue'),
                        meta: {
                            title: '订单',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'purchaseRecord',
                        name: 'purchaseRecord',
                        component: () => import('@/views/commonPage/buyManage/purchaseRecord/index'),
                        meta: {
                            title: '采购记录',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'arrivalOrder',
                        name: 'arrivalOrder',
                        // component: () => import('@/views/commonPage/buyManage/arrivalOrder/index'),
                        component: () => import('@/views/commonPage/buyManage/arrivalOrderSheet/index'),
                        meta: {
                            title: '收货单',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'arrivalOrder/qualityManagement',
                        name: 'arrivalOrderManager',
                        component: () => import('@/views/commonPage/buyManage/arrivalOrder/index.manager'),
                        meta: {
                            title: '收货单',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'inspectionOrder',
                        name: 'inspectionOrder',
                        // component: () => import('@/views/commonPage/buyManage/inspectionOrder/index'),
                        component: () => import('@/views/commonPage/buyManage/inspectionSheet/index'),
                        meta: {
                            title: '验货单',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'inspectionOrder/qualityManagement',
                        name: 'inspectionOrderManager',
                        component: () => import('@/views/commonPage/buyManage/inspectionOrder/index.manager'),
                        meta: {
                            title: '验货单',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'salesOrder',
                        name: 'salesOrder',
                        component: () => import('@/views/commonPage/salesManage/order'),
                        meta: {
                            title: '订单',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'salesManageSales',
                        name: 'salesManageSales',
                        component: () => import('@/views/commonPage/salesManage/sales'),
                        meta: {
                            title: '销售单',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'advanceSale',
                        name: 'advanceSale',
                        component: () => import('@/views/commonPage/advanceSale/index.js'),
                        meta: {
                            title: '预售单',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'advanceSaleFinance',
                        name: 'advanceSaleFinance',
                        component: () => import('@/views/commonPage/advanceSale/index.finance.js'),
                        meta: {
                            title: '预售单',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'licRecord/dealerLic',
                        name: 'dealerLic',
                        component: () => import('@/views/commonPage/licRecord/dealerLic'),
                        meta: {
                            title: '资证管理 - 经销商',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'licRecord/vendorLic',
                        name: 'vendorLic',
                        component: () => import('@/views/commonPage/licRecord/vendorLic'),
                        meta: {
                            title: '资证管理 - 厂商',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'licRecord/hospitalLic',
                        name: 'hospitalLic',
                        component: () => import('@/views/commonPage/licRecord/hospitalLic'),
                        meta: {
                            title: '资证管理 - 医院',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'licRecord/validityWarning',
                        name: 'validityWarning',
                        component: () => import('@/views/commonPage/licRecord/validityWarning'),
                        meta: {
                            title: '资证管理 - 效期预警',
                            requiresAuth: true
                        }
                    },
                    // 经销商
                    {
                        path: 'licRecord/myLic/dealer',
                        name: 'dealerLicManage',
                        component: () => import('@/views/commonPage/licRecord/myLic/dealerLicManage'),
                        meta: {
                            title: '资证管理 - 我的资证',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'toolsKits',
                        name: 'CommonToolsKits',
                        component: () => import('@/views/ptm/productManage/toolsKits'),
                        meta: {
                            title: '工具包',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'operationKits',
                        name: 'CommonOperationKits',
                        component: () => import('@/views/ptm/productManage/operationKits'),
                        meta: {
                            title: '手术包',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'operationPath',
                        name: 'CommonOperationPath',
                        component: () => import('@/views/ptm/productManage/operationPath'),
                        meta: {
                            title: '手术径路',
                            requiresAuth: true
                        }
                    },
                    // 医院
                    {
                        path: 'licRecord/myLic/hospital',
                        name: 'hospitalLicManage',
                        component: () => import('@/views/commonPage/licRecord/myLic/hospitalLicManage'),
                        meta: {
                            title: '资证管理 - 我的资证',
                            requiresAuth: true
                        }
                    },
                    // 厂商
                    {
                        path: 'licRecord/myLic/vendor',
                        name: 'vendorLicManage',
                        component: () => import('@/views/commonPage/licRecord/myLic/vendorLicManage'),
                        meta: {
                            title: '资证管理 - 我的资证',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'expiryDateManage',
                        name: 'expiryDateManage',
                        component: () => import('@/views/commonPage/expiryDateManage/list'),
                        meta: {
                            title: '资证效期预警',
                            requiresAuth: true
                        }
                    },
                    // 代储代配-租户管理
                    {
                        path: 'warehouseLeasing/lesseeManage',
                        name: 'lesseeManage',
                        component: () => import('@/views/commonPage/warehouseLeasing/lesseeManage/index.vue'),
                        meta: {
                            title: '租户管理',
                            requiresAuth: true
                        }
                    },
                    // 代储代配-入库通知
                    {
                        path: 'warehouseLeasing/grnNotice',
                        name: 'grnNotice',
                        component: () => import('@/views/commonPage/warehouseLeasing/grnNotice/index.vue'),
                        meta: {
                            title: '入库通知',
                            requiresAuth: true
                        }
                    },
                    // 代储代配-出库通知
                    {
                        path: 'warehouseLeasing/odoNotice',
                        name: 'odoNotice',
                        component: () => import('@/views/commonPage/warehouseLeasing/odoNotice/index.vue'),
                        meta: {
                            title: '出库通知',
                            requiresAuth: true
                        }
                    },
                    // 代储代配-物流管理
                    {
                        path: 'warehouseLeasing/logisticsManage',
                        name: 'logisticsManage',
                        component: () => import('@/views/commonPage/warehouseLeasing/logisticsManage/index.vue'),
                        meta: {
                            title: '物流管理',
                            requiresAuth: true
                        }
                    },
                    // 代储代配-库区管理
                    {
                        path: 'warehouseLeasing/stockArea',
                        name: 'stockArea',
                        component: () => import('@/views/commonPage/warehouseLeasing/stockAreaManage/index.vue'),
                        meta: {
                            title: '库区管理',
                            requiresAuth: true
                        }
                    },
                    /* 发票管理区域开始 */
                    // 1. 业务人员使用 (采购人员和销售人员)
                    {
                        path: 'invoiceManage/businessPeople/systemInvoice/inbound',
                        name: 'inboundSystemInvoice',
                        component: () =>
                            import('@/views/commonPage/invoiceManage/businessPeople/systemInvoice/inbound.vue'),
                        meta: {
                            title: '入库发票',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'invoiceManage/businessPeople/systemInvoice/outbound',
                        name: 'outboundSystemInvoice',
                        component: () =>
                            import('@/views/commonPage/invoiceManage/businessPeople/systemInvoice/outbound.vue'),
                        meta: {
                            title: '出库发票',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'invoiceManage/businessPeople/advancePayment',
                        name: 'advancePayment',
                        component: () =>
                            import('@/views/commonPage/invoiceManage/businessPeople/advancePayment/index.vue'),
                        meta: {
                            title: '预付款',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'invoiceManage/grnInvoiceSearch',
                        name: 'grnSearch',
                        component: () => import('@/views/commonPage/invoiceSearch/grn.vue'),
                        meta: {
                            title: '采购开票',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'invoiceManage/odoInvoiceSearch',
                        name: 'odoSearch',
                        component: () => import('@/views/commonPage/invoiceSearch/odo.vue'),
                        meta: {
                            title: '销售开票',
                            requiresAuth: true
                        }
                    },
                    // 2. 财务人员使用
                    {
                        path: 'invoiceManage/financeStaff/systemInvoice/inboundAndOutbound',
                        name: 'inboundSystemInvoiceFinance',
                        component: () =>
                            import(
                                '@/views/commonPage/invoiceManage/financeStaff/systemInvoice/inboundAndOutbound.vue'
                            ),
                        meta: {
                            title: '出入库发票',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'invoiceManage/financeStaff/advancePayment',
                        name: 'advancePaymentFinance',
                        component: () =>
                            import('@/views/commonPage/invoiceManage/financeStaff/advancePayment/index.vue'),
                        meta: {
                            title: '预付款',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'invoiceManage/financeStaff/paymentAttachment/paymentOrder',
                        name: 'paymentOrderFinance',
                        component: () =>
                            import('@/views/commonPage/invoiceManage/financeStaff/paymentAttachment/paymentOrder.vue'),
                        meta: {
                            title: '付款单',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'invoiceManage/financeStaff/paymentAttachment/receiveOrder',
                        name: 'receiveOrderFinance',
                        component: () =>
                            import('@/views/commonPage/invoiceManage/financeStaff/paymentAttachment/receiveOrder.vue'),
                        meta: {
                            title: '收款单',
                            requiresAuth: true
                        }
                    },
                    /* 发票管理区域结束 */
                    {
                        path: 'product/barcodeManage',
                        name: 'barcodeManage',
                        component: () => import('@/views/commonPage/barcodeManage/index.vue'),
                        meta: {
                            title: '标签管理',
                            requiresAuth: true
                        }
                    }
                ]
            },
            {
                path: '/hospital',
                name: 'hospital',
                component: () => import('@/views/hospital/hospital'),
                meta: {
                    title: '医院管理相关页面',
                    requiresAuth: true
                },
                children: [
                    {
                        path: 'SuppliesClaim',
                        name: 'SuppliesClaim',
                        component: () => import('@/views/hospital/consumables/SuppliesClaim'),
                        meta: {
                            title: '申领',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'dispatch',
                        name: 'dispatch',
                        component: () => import('@/views/hospital/consumables/dispatch'),
                        meta: {
                            title: '发放',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'hospitalHome',
                        name: 'hospitalHome',
                        component: () => import('@/views/hospital/hospitalHome'),
                        meta: {
                            title: '首页',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'purchaseGrn',
                        name: 'hospitalPurchaseGrn',
                        // component: () => import('@/views/hospital/purchaseGrn/index.vue'),
                        // component: () => import('@/views/hospital/purchaseGrnNew/index.vue'),
                        component: () => import('@/views/hospital/purchaseGrnSheet/index.vue'),
                        meta: {
                            title: '采购入库',
                            requiresAuth: true
                        }
                    },
                    {
                        path: 'premiumPrice',
                        name: 'hospitalPremiumPrice',
                        component: () => import('@/views/hospital/premiumPrice/index.vue'),
                        meta: {
                            title: '溢价规则',
                            requiresAuth: true
                        }
                    }
                ]
            }
        ]
    },
    {
        path: '/error',
        name: 'Exception',
        component: () => import('@/components/Exception'),
        meta: {
            title: '404',
            requiresAuth: false
        }
    },
    {
        path: '*',
        redirect: '/error'
    } // 全不匹配的情况下，返回404，路由按顺序从上到下，依次匹配。最后一个*能匹配全部，
    /* {
        path: '/test',
        name: 'test',
        component: () => import('./views/test'),
        meta: {
            requireAuth: true
        }
    } */
];

// 页面刷新时,重新赋值token
// if(Vue.cookie.get('token')) {
//   store.commit('setToken', 'token');  // 保证页面刷新token仍有效
// }

const router = new Router({
    routes
});

router.beforeEach((to, from, next) => {
    // if (to.meta.title) {
    //     document.title = to.meta.title
    // }
    if (to.matched.some((r) => r.meta.requiresAuth)) {
        // 判断是否已经登录
        if (router.app.$cookie.get('loginStatus') || store.state.isLogin) {
            next();
        } else {
            next({
                path: '/Login',
                query: { redirect: to.fullPath }
            });
        }
    } else {
        next();
    }
});
export default router;
