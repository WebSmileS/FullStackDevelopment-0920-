<template>
    <a-row class="and-page-layout">
        <a-row class="apl-breadcrumb" style="height: 21px">
            <slot name="breadcrumb"></slot>
        </a-row>
        <a-row class="apl-content">
            <slot name="tabs-content"></slot>
        </a-row>
        <a-row class="apl-modals">
            <slot name="modals"></slot>
        </a-row>
    </a-row>
</template>

<script>
/**
 * Consists of Breadcrumb, Tabs Content, Modals
 */
export default {
    name: 'AndPageLayout',
    data() {
        return {};
    },
    computed: {
    }
};
</script>

<style lang="less">
/* style ant - start - */
.ant-modal.choose_org_md {
    left: calc(50% - 300px);
}
/* style ant - end - */
.and-page-layout {
    /* style ant - start - */
    .ant-select {
        .ant-select-selection {
            width: 100%;
        }
        .ant-select-selection__rendered {
            width: 100%;
        }
    }
    .ant-input {
        height: 24px;
    }
    .ant-select .ant-select-selection,
    .ant-input {
        border-radius: 0;
        border-top: none;
        border-left: none;
        border-right: none;
        background: inherit;
    }
    .ant-input:focus {
        box-shadow: none;
    }
    /* style ant - end - */

    .apl-content {
        margin-top: 15px;
    }

    .and-tabs-content {
        .atc-radio-btns {
            text-align: left;

            .ant-radio-group {
                margin-left: 10px;

                .ant-radio-button-wrapper {
                    border-radius: 0;
                    margin-bottom: 0.5;
                    border-bottom: none;
                }
            }
        }
        .atc-content {
            height: calc(100vh - 125px);
            padding: 8px;
            background: white;
            border-radius: 2px;
            border: 1px solid #d2d2d2;

            /* style handsontable - start - */
            .atc-pane {
                height: 100%;
            }
            .ht_master {
                width: 100% !important;
                .wtHolder {
                    width: 100% !important;
                }
                .htCore {
                    z-index: 9999;
                }
            }
            .htColumnHeaders {
                height: 100%;
                .ht_master {
                    height: 100%;
                    .wtHolder {
                        height: 100% !important;
                    }
                }
            }
            /* style handsontable - end - */

            .atc-header {
                margin: 0;
            }
            .and-dropdown {
                position: absolute;
                min-height: 24px;
                max-height: 150px;
                overflow-y: auto;
                z-index: 10000;
                background: #fff;
                border: 1px solid #ccc;
                .and-dropdown-option {
                    cursor: pointer;
                    padding: 5px 3px;
                    white-space: nowrap;
                    overflow: hidden !important;
                    text-overflow: ellipsis;
                    &.hover {
                        background-color: #e6f7ff;
                    }
                }
            }
            .and-cascader {
                position: absolute;
                opacity: 0;
            }
            .ant-cascader-menus {
                z-index: 10000;
            }
            .and-hottable-acts {
                position: absolute;
                z-index: 1000;
                right: 0;
                top: 1px;
                background: white;
                .and-ha-hammer {
                    width: 26px;
                    height: 26px;
                    position: absolute;
                    background: #f0f0f0;
                    right: 0;
                    &:hover {
                        color: #1890ff;
                    }
                }
                .and-ha-dropdown {
                    position: absolute;
                    right: 0;
                    width: 180px;
                    top: 22px;
                    text-align: left;
                    background: #fff;
                    border: 1px solid #e8eaec;
                    box-shadow: 0 1px 6px #e8eaec;
                    cursor: pointer;
                    .and-had-option {
                        padding: 5px 10px;
                        .ant-checkbox-wrapper {
                            width: 100%;
                        };
                        &:hover {
                            background-color: #e6f7ff;
                        }
                    }
                    .and-had-group {
                        max-height: 250px;
                        overflow-y: auto;
                    }
                    .and-had-border {
                        border-bottom: 1px solid #e8eaec;
                    }
                    .and-had-acts {
                        height: 31px;
                        padding: 5px 0;
                        text-align: center;
                        & > div:hover {
                            color: #1890ff;
                        }
                    }
                }
            }

            .atc-footer {
                margin-top: 0px;
            }
        }
    }
    &.enquiry-sheets {
        .add-atc-content {
            .atc-header {
                margin-top: 5px;
                margin-bottom: 10px;
            }
            /* handsontable - height */
            .atc-pane {
                height: calc(100% - 153px);
            }
        }
    }
    &.plan-sheets {
        .add-atc-content {
            .atc-header {
                margin-top: 0px;
                margin-bottom: 10px;
            }
            /* handsontable - height */
            .atc-pane {
                height: calc(100% - 177px);
            }
        }
    }
    &.order-sheets {
        .add-atc-content {
            .atc-header {
                margin-top: 0px;
                margin-bottom: 10px;
            }
            /* handsontable - height */
            .atc-pane {
                height: calc(100% - 187px);
            }
        }
    }
    &.grn-sheets {
        .add-atc-content {
            .atc-header {
                margin-top: 0px;
                margin-bottom: 10px;
            }
            /* handsontable - height */
            .atc-pane {
                height: calc(100% - 172px);
            }
        }
    }
    &.return-sheets {
        .add-atc-content {
            .atc-header {
                margin-top: 0px;
                margin-bottom: 10px;
            }
            /* handsontable - height */
            .atc-pane {
                height: calc(100% - 182px);
            }
        }
    }
    &.arrival-sheets {
        .add-atc-content {
            .atc-header {
                margin-top: 0px;
                margin-bottom: 10px;
            }
            /* handsontable - height */
            .atc-pane {
                height: calc(100% - 173px)
            }
        }
    }
}
</style>