<!--suppress ALL -->
<template>
    <div class="product-page">
        <div class="todo-part">
            <a-row>
                <a-col :span="12" class="btn-count">
                    <a-button v-if="nowSystemType != 3" class="btn-nom mgr-10" type="primary" @click="openEdit"
                        >修改</a-button
                    >
                    <a-button v-if="nowSystemType != 3" class="btn-nom mgr-10" @click="deleteBtn" type="danger"
                        >删除</a-button
                    >
                    <a-button
                        v-if="nowSystemType != 3"
                        class="btn-nom"
                        @click="handleSync"
                        type="primary"
                        :disabled="!sync.shouldSync"
                        >同步</a-button
                    >
                </a-col>
                <a-col :span="12" class="align-r">
                    <a-input
                        v-if="!highSearch"
                        placeholder="产品"
                        v-model="searchData.name"
                        size="small"
                        class="input-nom"
                        style="width: 70%"
                    >
                        <a-button
                            @click="initPage()"
                            class="btn-input"
                            icon="search"
                            slot="suffix"
                            type="primary"
                        ></a-button>
                    </a-input>
                    <div class="adv-search mgl-20">
                        <a-tooltip placement="left" title="高级搜索">
                            <a-icon v-if="!highSearch" type="down-circle" @click="highSearch = true" />
                            <a-icon v-else type="up-circle" @click="highSearch = false" />
                        </a-tooltip>
                    </div>
                </a-col>
            </a-row>
            <a-row class="high-search-box pdl-10" :class="{ hide: !highSearch }">
                <a-col :span="24" class="col-list">
                    <!-- 高级搜索 -->
                    <ul class="form">
                        <li>
                            <span class="label">产品</span>
                            <a-input type="text" v-model="searchData.name" placeholder="" />
                        </li>
                        <li style="position: relative" v-if="nowSystemType != 3">
                            <span class="label">厂商</span>
                            <a-input readonly="readonly" class="input" @click="openChooseTb" v-model="vendorObj.name" />
                            <span v-if="vendorObj.name != ''" @click="clearSearchDataVenFn()" class="close">X</span>
                        </li>
                        <li>
                            <span class="label">注册证号</span>
                            <a-input type="text" v-model="searchData.register_sn" placeholder="" />
                        </li>
                        <li>
                            <span class="label">类型</span>
                            <a-cascader
                                v-model="searchData.type_inner_sn"
                                :options="categoryList"
                                :loadData="loadCategory"
                                placeholder=""
                                :displayRender="displayRender"
                            />
                        </li>
                        <li>
                            <span class="label" style="width: 60px">产品分类</span>
                            <a-cascader
                                v-model="searchData.code68_sn"
                                :options="op68List"
                                changeOnSelect
                                :loadData="load68data"
                                placeholder=""
                                :displayRender="displayRender"
                            />
                        </li>
                        <li>
                            <a-button class="btn-nom" type="primary" @click="initPage()"> 搜索 </a-button>
                        </li>
                    </ul>
                </a-col>
            </a-row>
        </div>
        <div class="box">
            <div class="boxtop">
                <div style="margin-top: 0" class="table-part">
                    <vxe-grid
                        ref="xTable"
                        size="small"
                        border
                        :columns="columns"
                        :height="boxTopHeight"
                        resizable
                        auto-resize
                        highlight-current-row
                        @cell-click="cellClickFn"
                        @cell-dblclick="cellDblclickFn"
                        align="center"
                        :data="tableData"
                        :loading="pageInit.loading"
                        :start-index="(pageInit.current - 1) * pageInit.pageSize"
                    >
                    </vxe-grid>
                    <vxe-pager
                        :loading="pageInit.loading"
                        :current-page="pageInit.current"
                        :page-size="pageInit.pageSize"
                        :page-sizes="pageInit.pageSizes"
                        :total="pageInit.total"
                        :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                        @page-change="changePaginationFn"
                    >
                    </vxe-pager>
                </div>
            </div>
            <div class="resize"></div>
            <div class="boxdown">
                <div
                    class="bottom-part more"
                    v-bind:style="{ height: boxDownHeight + 8 + 'px' }"
                    style="padding: 0px 4px"
                >
                    <div class="spec-box table-part" v-bind:style="{ height: boxDownHeight - 4 + 'px' }">
                        <div class="todo_box" style="border-bottom: none">
                            <a-button class="btn-nom mgr-10" @click="openCreateSpec" type="primary">新增</a-button>
                            <a-button class="btn-nom mgr-10" @click="openEditSpec" type="primary">修改</a-button>
                            <!--<a-button class="btn-nom cut mgr-10" @click="deleteSpecBtn" type="danger">删除</a-button>-->
                            <a-button class="btn-nom mgr-10" @click="openPackUnit" type="primary">包装单位</a-button>
                            <a-input
                                @keyup.enter="handleSearchSpecification"
                                v-model="specificationSearchVal"
                                placeholder="产品编码/规格型号"
                                size="small"
                                class="input-nom"
                                style="width: 40%; vertical-align: top"
                            >
                                <a-button
                                    @click="handleSearchSpecification"
                                    icon="search"
                                    slot="suffix"
                                    type="primary"
                                    class="btn-input"
                                ></a-button>
                            </a-input>
                        </div>
                        <div style="width: 100%; margin: 54px 0 0; padding: 0 10px">
                            <!-- 此表格调用platformProductSpecList获取数据 -->
                            <vxe-grid
                                v-if="nowSystemType === 0"
                                ref="specTable"
                                size="small"
                                :columns="spec_columns_test"
                                :height="boxDownHeight - 70"
                                border
                                resizable
                                auto-resize
                                highlight-current-row
                                @cell-click="specClickFn"
                                @cell-dblclick="specDblclickFn"
                                align="center"
                                :data="specData"
                            >
                            </vxe-grid>
                            <vxe-grid
                                v-if="nowSystemType === 3"
                                ref="specTable"
                                size="small"
                                :columns="spec_columns_vendor"
                                :height="boxDownHeight - 60"
                                border
                                resizable
                                auto-resize
                                highlight-current-row
                                @cell-click="specClickFn"
                                @cell-dblclick="specDblclickFn"
                                align="center"
                                :data="specData"
                            >
                            </vxe-grid>
                        </div>
                    </div>
                    <div class="resizeHoriz"></div>
                    <div class="tab-table-wrap minimal more2" style="height: calc(100% - 3px); margin-left: 8px">
                        <bottom-tabs-wrapper>
                            <!-- tab切换区域开始 -->
                            <a-tabs class="align-l" style="padding-top: 0; height: calc(100% - 8px)" type="card">
                                <a-tab-pane v-for="item of tabName" :key="item.key" :tab="item.name">
                                    <div v-if="!isChooseTr" class="no-data-box">
                                        <div><a-icon type="file-excel" /></div>
                                        请选择商品
                                    </div>
                                    <template v-else>
                                        <!-- 产品图片 -->
                                        <div class="bottom-tabs-container" v-if="item.key == 'goodsPic'">
                                            <div class="bottom-tabs-content-left bottom-buttons-vertical-wrapper">
                                                <a-upload
                                                    :action="QiniuUrl"
                                                    :show-upload-list="false"
                                                    :multiple="true"
                                                    :beforeUpload="checkFile"
                                                    :supportServerRender="true"
                                                    :data="upLoadparams"
                                                    @change="upGoodsImg"
                                                >
                                                    <div>
                                                        <a-button class="btn-nom mgr-10" type="primary"
                                                            >上传图片</a-button
                                                        >
                                                    </div>
                                                </a-upload>
                                            </div>
                                            <div class="bottom-tabs-content-right bottom-pictures-wrapper">
                                                <bottom-pictures
                                                    :list="goodsImgList"
                                                    @previewImg="previewImg"
                                                    @delete="deleteImg"
                                                />
                                            </div>
                                        </div>
                                        <!-- 注册证登记表 -->
                                        <div class="bottom-tabs-container" v-if="item.key == 'regForm'">
                                            <div class="bottom-tabs-content-left bottom-buttons-vertical-wrapper">
                                                <a-button @click="openGicMdrf" class="btn-nom mgr-10" type="primary">
                                                    新增
                                                </a-button>
                                                <a-button @click="editGicMdrf" class="btn-nom mgr-10" type="primary">
                                                    修改
                                                </a-button>
                                                <a-button @click="delGicMdrf" class="btn-nom mgr-10" type="danger">
                                                    删除
                                                </a-button>
                                            </div>
                                            <div class="bottom-tabs-content-right bottom-table-wrapper">
                                                <vxe-grid
                                                    ref="mdrfTable"
                                                    size="small"
                                                    border
                                                    :columns="mdrf_columns"
                                                    :height="boxDownHeight - 72"
                                                    resizable
                                                    auto-resize
                                                    highlight-current-row
                                                    @cell-click="mdrfClickFn"
                                                    @cell-dblclick="mrdfDblclickFn"
                                                    align="center"
                                                    :data="mdrfData"
                                                >
                                                </vxe-grid>
                                            </div>
                                        </div>
                                        <!-- 检测报告 -->
                                        <div class="bottom-tabs-container" v-if="item.key == 'test'">
                                            <bottom-pictures
                                                :list="examImgList"
                                                @previewImg="previewImg"
                                                @delete="deleteImg"
                                            />
                                        </div>
                                        <!-- 营业执照 -->
                                        <div class="bottom-tabs-container" v-if="item.key === 'bizLicense'">
                                            <bottom-pictures
                                                :list="bizLicenseImgList"
                                                :deleteAble="false"
                                                @previewImg="previewImg"
                                            />
                                        </div>
                                        <!-- 生产许可证 -->
                                        <div class="bottom-tabs-container" v-if="item.key === 'prodLicense'">
                                            <bottom-pictures
                                                :list="prodLicenseImgList"
                                                :deleteAble="false"
                                                @previewImg="previewImg"
                                            />
                                        </div>
                                        <!-- 其他 -->
                                        <div class="bottom-tabs-container" v-if="item.key == 'other'">
                                            <div class="bottom-tabs-content-left bottom-buttons-vertical-wrapper">
                                                <a-upload
                                                    :action="QiniuUrl"
                                                    :show-upload-list="false"
                                                    :multiple="true"
                                                    :beforeUpload="checkFile"
                                                    :supportServerRender="true"
                                                    :data="upLoadparams"
                                                    @change="upOtherImg"
                                                >
                                                    <div>
                                                        <a-button class="btn-nom mgr-10" type="primary"
                                                            >上传图片</a-button
                                                        >
                                                    </div>
                                                </a-upload>
                                            </div>
                                            <div class="bottom-tabs-content-right bottom-pictures-wrapper">
                                                <bottom-pictures
                                                    :list="otherImgList"
                                                    @previewImg="previewImg"
                                                    @delete="deleteImg"
                                                />
                                            </div>
                                        </div>
                                    </template>
                                </a-tab-pane>
                            </a-tabs>
                        </bottom-tabs-wrapper>
                    </div>
                </div>
            </div>
        </div>
        <!-- 模态框区域 -->
        <a-modal
            title="图片预览"
            :visible="preImgSt"
            :width="previewWid"
            okText="打印"
            cancelText="关闭"
            @ok="printFn"
            @cancel="closePreview"
            :maskClosable="false"
            :centered="true"
            class="modal-form-input-scoped global-drag imgModal"
        >
            <div v-globalDrag="{ el: 'imgModal' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ leftTitle }} - 图片预览
                </h6>
            </div>
            <div ref="printData" class="preview-box">
                <img :src="imgSrc" />
            </div>
        </a-modal>
        <a-modal
            title="新增"
            :visible="createSt"
            :width="900"
            okText="保存"
            @cancel="createSt = false"
            @ok="saveGoodsFn"
            :maskClosable="false"
            :centered="true"
            class="modal-form-input-scoped global-drag create"
        >
            <div v-globalDrag="{ el: 'create' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ leftTitle }} - 新增
                </h6>
            </div>
            <create-cp ref="createPage" @getData="getCpmData"></create-cp>
        </a-modal>
        <a-modal
            title="修改"
            :visible="editSt"
            :width="900"
            okText="保存"
            @cancel="editSt = false"
            @ok="editGoodsFn"
            :maskClosable="false"
            :centered="true"
            class="modal-form-input-scoped global-drag edit editModel"
        >
            <div v-globalDrag="{ el: 'edit' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ leftTitle }} - 修改
                </h6>
            </div>
            <edit-cp ref="editPage" :modalData="editTrObj" @getData="getEditData" @getEditInfo="sendEditInfo"></edit-cp>
            <!--:modalData="editTrObj"-->
        </a-modal>
        <a-modal
            title="录入"
            :visible="entrySt"
            :width="1150"
            :maskClosable="false"
            :centered="true"
            @cancel="entrySt = false"
            class="modal-form-input-scoped global-drag entryModal"
        >
            <div v-globalDrag="{ el: 'entryModal' }" class="modal-header-wrap">
                <h6 v-if="mdrfIsNew">
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ leftTitle }} - 医疗器械注册证（登记表） - 新增
                </h6>
                <h6 v-else>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ leftTitle }} - 医疗器械注册证（登记表） - 修改
                </h6>
            </div>
            <entry-gic
                ref="mdrfGicPage"
                @getData="getMdrfGicData"
                @returnOtherCertImgs="getOtherCertImgsForPrint"
                :gicMdrfObj="gicMdrfObj"
            ></entry-gic>
            <template slot="footer">
                <a-button @click="entrySt = false">
                    <span>取消</span>
                </a-button>
                <a-button @click="handlePrint">
                    <span>打印</span>
                </a-button>
                <a-button type="primary" @click="entryGicMdrf">
                    <span>保存</span>
                </a-button>
            </template>
        </a-modal>
        <a-modal
            :title="specTit"
            :visible="specSt"
            :maskClosable="false"
            :centered="true"
            :width="540"
            okText="保存"
            @ok="saveSpec"
            @cancel="specSt = false"
            class="modal-form-input-scoped global-drag unit_md"
        >
            <div v-globalDrag="{ el: 'unit_md' }" class="modal-header-wrap">
                <h6 v-if="!isEdit">
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ leftTitle }} - 规格型号 - 新增
                </h6>
                <h6 v-else>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ leftTitle }} - 规格型号 - 修改
                </h6>
            </div>
            <div class="upload-count">
                <li class="">
                    <span class="label" style="margin-right: 10px"><em class="em red">*</em>规格型号</span>
                    <a-input placeholder="" v-model="newSpecData.spec" style="width: 80%" class="input" />
                </li>
                <li style="padding-top: 24px">
                    <span class="label" style="margin-right: 10px">产品编码</span>
                    <a-input placeholder="" v-model="newSpecData.pvd" style="width: 80%" class="input" />
                </li>
                <li style="padding-top: 24px" v-if="nowSystemType === 3">
                    <span class="label" style="margin-right: 10px; vertical-align: middle">医保编号</span>
                    <a-dropdown :trigger="['click']">
                        <a-input
                            class="input"
                            style="width: 73%"
                            v-model="newSpecData.healthCareSn"
                            @change="healthCareSnChangeFn"
                            @click="getProductSnAPIFn(true)"
                        >
                            <a-icon slot="suffix" type="down" />
                        </a-input>
                        <a-menu
                            v-if="healthAndSunSnModal.data.length"
                            slot="overlay"
                            style="maxheight: 200px; overflow: auto"
                        >
                            <a-menu-item
                                v-for="item in healthAndSunSnModal.data"
                                :key="item.index"
                                @click="healthCareSnChangeFn(item)"
                            >
                                <a href="javascript:;">{{ item.sn }}</a>
                            </a-menu-item>
                        </a-menu>
                    </a-dropdown>
                    <a-tooltip placement="topLeft" title="添加医保编号">
                        <a-button
                            :disabled="!isEdit"
                            @click="onHealthAndSunSnMore(true)"
                            style="width: 22px; height: 22px; padding: 0px; margin-left: 10px"
                        >
                            <a-icon type="plus" />
                        </a-button>
                    </a-tooltip>
                </li>
                <li style="padding-top: 24px" v-if="nowSystemType === 3">
                    <span class="label" style="margin-right: 10px; padding-right: 5px; vertical-align: middle"
                        >阳光流水号</span
                    >
                    <a-dropdown :trigger="['click']">
                        <a-input
                            class="input"
                            style="width: 73%"
                            v-model="newSpecData.sunSn"
                            @change="sunSnChangeFn"
                            @click="getProductSnAPIFn(false)"
                        >
                            <a-icon slot="suffix" type="down" />
                        </a-input>
                        <a-menu
                            v-if="healthAndSunSnModal.data.length"
                            slot="overlay"
                            style="maxheight: 200px; overflow: auto"
                        >
                            <a-menu-item
                                v-for="item in healthAndSunSnModal.data"
                                :key="item.index"
                                @click="sunSnChangeFn(item)"
                            >
                                <a href="javascript:;">{{ item.sn }}</a>
                            </a-menu-item>
                        </a-menu>
                    </a-dropdown>
                    <a-tooltip placement="topLeft" title="添加阳光流水号">
                        <a-button
                            :disabled="!isEdit"
                            @click="onHealthAndSunSnMore(false)"
                            style="width: 22px; height: 22px; padding: 0px; margin-left: 10px"
                        >
                            <a-icon type="plus" />
                        </a-button>
                    </a-tooltip>
                </li>
                <li style="padding-top: 24px">
                    <span class="label" style="width: 96px">有效</span>
                    <a-checkbox @change="changeSwitch" :checked="switchType" />
                </li>
            </div>
        </a-modal>
        <a-modal
            :title="packUnitName"
            :visible="packUnitSt"
            :width="900"
            :maskClosable="false"
            :centered="true"
            @cancel="packUnitSt = false"
            class="modal-form-input-scoped global-drag pack_unit"
        >
            <div v-globalDrag="{ el: 'pack_unit' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ leftTitle }} - 规格型号 - 包装单位
                </h6>
            </div>
            <template slot="footer">
                <div class="modal-tip" style="color: red">提示：建立单位计量关系时，请从“最小单位”开始。</div>
                <a-button key="back" @click="packUnitSt = false">关闭</a-button>
            </template>
            <pack-unit
                ref="pageUnitPg"
                :packUnitObj="packUnitObj"
                @handlePackUnitSaveDone="handlePackUnitSaveDoneFn"
            ></pack-unit>
            <!-- <pack-unit ref="pageUnitPg" :packUnitObj="packUnitObj" @handlePackUnitSaveDone="loadSpecFn(chooseTrSpecObj)"></pack-unit> -->
        </a-modal>
        <a-modal
            :title="chooseOrgMd.title"
            :visible="chooseOrgMd.alert"
            :maskClosable="false"
            :width="600"
            :centered="true"
            :footer="null"
            @cancel="chooseOrgMd.alert = false"
            class="modal-form-input-scoped"
        >
            <choose-org-tab
                ref="chooseOrgMd"
                @chooseOrg="chooseOrgFn"
                :partType="partType"
                :pageTypeName="1"
            ></choose-org-tab>
        </a-modal>
        <!-- 医保编号详情窗口 -->
        <a-modal
            :visible="healthAndSunSnModal.visible"
            :maskClosable="false"
            :centered="true"
            :width="540"
            @cancel="healthAndSunSnModal.visible = false"
            class="modal-form-input-scoped global-drag health_and_sun_md"
        >
            <div v-globalDrag="{ el: 'health_and_sun_md' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ healthAndSunSnModal.title }}
                </h6>
            </div>
            <!-- 头部按钮区域 -->
            <div style="margin-top: 50px">
                <a-button @click="groupBtnFn('add')" type="primary" size="small" style="margin-right: 20px; width: 80px"
                    >新增</a-button
                >
                <a-button
                    @click="groupBtnFn('edit')"
                    :disabled="!healthAndSunAddModal.objSelected"
                    type="primary"
                    size="small"
                    style="margin-right: 20px; width: 80px"
                    >修改</a-button
                >
                <a-button
                    @click="groupBtnFn('delete')"
                    :disabled="!healthAndSunAddModal.objSelected"
                    type="danger"
                    size="small"
                    style="margin-right: 20px; width: 80px"
                    >删除</a-button
                >
            </div>
            <!-- 表格区域 -->
            <div style="margin-top: 20px">
                <vxe-grid
                    v-if="isHealthCareModal"
                    ref="healthCareSnTable"
                    size="small"
                    :columns="healthAndSunSnModal.health_care_columns"
                    :height="400"
                    border
                    resizable
                    auto-resize
                    highlight-current-row
                    @cell-click="Table_ClickFn"
                    @cell-dblclick="Table_DblClickFn"
                    align="center"
                    :data="healthAndSunSnModal.data"
                >
                </vxe-grid>
                <vxe-grid
                    v-else
                    ref="sunSnTable"
                    size="small"
                    :columns="healthAndSunSnModal.sun_columns"
                    :height="400"
                    border
                    resizable
                    auto-resize
                    highlight-current-row
                    @cell-click="Table_ClickFn"
                    @cell-dblclick="Table_DblClickFn"
                    align="center"
                    :data="healthAndSunSnModal.data"
                >
                </vxe-grid>
            </div>
            <template slot="footer">
                <a-button @click="onHealthAndSunAddCancel">
                    <span>关闭</span>
                </a-button>
            </template>
        </a-modal>
        <a-modal
            :title="healthAndSunAddModal.title"
            :visible="healthAndSunAddModal.visible"
            :maskClosable="false"
            :centered="true"
            :width="540"
            @cancel="healthAndSunAddModal.visible = false"
            class="modal-form-input-scoped global-drag health_and_sun_add_md"
        >
            <div v-globalDrag="{ el: 'health_and_sun_md' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ healthAndSunAddModal.title }}
                </h6>
            </div>
            <li>
                <span v-if="isHealthCareModal" class="label" style="margin-right: 10px"
                    ><em class="em red">*</em>医保编号</span
                >
                <span v-if="!isHealthCareModal" class="label" style="margin-right: 10px"
                    ><em class="em red">*</em>阳光流水号</span
                >
                <a-input placeholder="" v-model="healthAndSunAddModal.input_sn" style="width: 80%" class="input" />
            </li>
            <li style="padding-top: 24px">
                <span class="label" style="width: 96px; margin-left: 8px">默认</span>
                <a-checkbox v-model="healthAndSunAddModal.is_default" style="margin-left: 36px" />
            </li>
            <template slot="footer">
                <a-button @click="healthAndSunAddModal.visible = false">
                    <span>取消</span>
                </a-button>
                <a-button type="primary" @click="onHealthAndSunSave">
                    <span>保存</span>
                </a-button>
            </template>
        </a-modal>
        <a-modal
            v-model="sync.visible"
            title="同步平台信息中"
            style="text-align: center; padding-top: 50px"
            :maskClosable="false"
            :closable="false"
        >
            <template slot="footer">
                <a-button @click="onSyncConfirm" :loading="sync.isSync">确认</a-button>
            </template>
            <div v-if="sync.isSync">
                <a-icon type="loading" style="font-size: 50px; color: #1890ff" />
                <p style="margin-top: 10px; font-size: 20px">同步中</p>
            </div>
            <div v-else>
                <a-icon type="check-circle" theme="twoTone" two-tone-color="#42b983" style="font-size: 50px" />
                <p style="margin-top: 10px; font-size: 20px">同步成功</p>
            </div>
        </a-modal>
    </div>
</template>

<script>
import BottomTabsWrapper from '@/components/BaseBottom/BottomTabsWrapper';
import BottomPictures from '@/components/BaseBottom/BottomPictures';
import createCp from './module/createGoods';
import editCp from './module/editGoods';
import entryGic from './module/entryGic';
import packUnit from './packUnit';
import chooseOrgTab from '@/components/chooseOrgTab';
import clickDebounce from '@/config/clickDebounce';

import {
    getVendorList,
    categoryGetNextListAPI,
    categoryGetParentListAPI,
    code68ParentListAPI,
    code68ChildrenListAPI,
    getUploadTokenAPI,
    platformProductAdd,
    platformProductPublicList,
    platformProductDelAPI,
    platformProductEditAPI,
    platformProductImgAddAPI,
    platformProductImgGetAPI,
    platformProductMdrfGet,
    platformProductMdrfAdd,
    platformProductMdrfEdit,
    platformProductMdrfInfo,
    platformProductSpecList,
    platformProductSpecAdd,
    platformProductSpecEdit,
    platformProductSpecDel,
    platformProductImgDelAPI,
    platformProductMdrfDel,
    platformOrgImgListAPI,
    platforProductSnListAPI,
    productSnAddAPI,
    productSnEditAPI,
    productSnDelAPI,
    platformProductSyncAPI
} from '@/service/pageAjax';

export default {
    name: 'productReleased',
    components: {
        BottomPictures,
        BottomTabsWrapper,
        createCp,
        editCp,
        entryGic,
        packUnit,
        chooseOrgTab
    },
    data() {
        return {
            QiniuUrl: localStorage.getItem('QiniuUrl'),
            specificationSearchVal: '', // 规格型号区域 产品编码/规格型号input查询
            boxTopHeight: 300,
            boxDownHeight: 300,
            leftTitle: `${this.$route.meta.title}`,
            nowSystemType: '',
            nowOid: '',
            tableHeight: 300,
            tabScrollHeight: null,
            vendorList: [],
            categoryList: [],
            op68List: [],
            partType: 'ven',
            chooseOrgMd: {
                title: `${this.$route.meta.title}  - 选择机构`,
                alert: false,
                loading: false
            },
            vendorObj: {
                name: ''
            },
            searchData: {
                name: '',
                register_sn: '', // 注册证号
                vendor_inner_sn: '', // 产商ID
                type_inner_sn: [], // 自定义分类内部编号
                code68_sn: [] // 医疗器械68分类编号
            },
            upToken: '',
            upLoadparams: {},
            highSearch: false,
            preImgSt: false,
            createSt: false,
            editSt: false,
            entrySt: false,
            packUnitSt: false,
            packUnitName: '',
            mdrfIsNew: true,
            editSwitch: true,
            mdrfSwitch: true,
            uploadGicSt: false,
            openType: '',
            specSt: false,
            specTit: '',
            modalData: [], // 模态数据
            editTrObj: {
                // 修改数据
                vid: '',
                pid: '',
                vendor_inner_sn_old: [], // 厂商ID
                type_inner_sn_old: [], // 自定义分类内部编号
                code68_sn_old: [], // 医疗器械68分类编号
                storage_condition_old: 1, // 存储条件
                health_care_type_old: 0, // 医保类型
                name_old: '',
                specifications_old: '',
                health_care_sn_old: '', // 医保编号
                description_old: ''
            }, // 修改回传数据
            gicMdrfObj: {
                address: '',
                application_range: null,
                contraindication: null,
                description: null,
                images: [],
                issue_date: null,
                issue_organization: null,
                new: true,
                performance_structure: null,
                product_name: '',
                production_address: null,
                register_sn: null,
                specifications: '',
                standard: null,
                vendor_name: ''
            },
            newMdrfGicData: {},
            imgSrc: '',
            previewWid: 300, // 图片预览宽度
            pageInit: {
                loading: false,
                total: 10,
                current: 1, // 对应后端的 pageNum
                pageSize: 10, // 对应后端的 pageSize
                pageSizes: [10, 20, 30, 40]
            },
            rowSelection: {
                hideDefaultSelections: false
            },
            chooseTrObj: {}, // 已选择对象
            isChooseTr: false,
            columns: [
                {
                    title: '产品',
                    dataIndex: 'name',
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    width: '280px',
                    slots: {
                        default: ({ row }) => {
                            return [row.name];
                        }
                    }
                },
                {
                    title: '厂商',
                    dataIndex: 'vendor_name',
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    width: '230px',
                    slots: {
                        default: ({ row }) => {
                            return [row.vendor_name];
                        }
                    }
                },
                {
                    title: '类型',
                    dataIndex: 'type_name',
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    width: '150px',
                    slots: {
                        default: ({ row }) => {
                            return [row.type_name];
                        }
                    }
                },
                {
                    id: 4,
                    title: '产品分类',
                    dataIndex: 'text_68',
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    width: '200px',
                    slots: {
                        default: ({ row }) => {
                            return [row.text_68];
                        }
                    }
                },
                {
                    title: '来自',
                    showOverflow: true,
                    dataIndex: 'sourceName',
                    showHeaderOverflow: true,
                    align: 'center',
                    width: '100px',
                    slots: {
                        default: ({ row }) => {
                            return [row.sourceName];
                        }
                    }
                },
                {
                    title: '状态',
                    dataIndex: 'status',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center',
                    width: '100px',
                    slots: {
                        default: ({ row }) => {
                            let val;
                            let auditSt;
                            if (parseInt(row.status) === 1) {
                                val = '有效';
                                auditSt = 'color: #16a935';
                            }
                            if (parseInt(row.status) === 0) {
                                auditSt = 'color: red';
                                val = '无效';
                            }
                            return [<span style={auditSt}>{val}</span>];
                        }
                    }
                }
            ],
            tableData: [],
            tabName: [
                {
                    name: '产品图片',
                    key: 'goodsPic'
                },
                {
                    name: '医疗器械注册证（登记表）',
                    key: 'regForm'
                },
                {
                    name: '质量检测报告',
                    key: 'test'
                },
                {
                    name: '营业执照',
                    key: 'bizLicense'
                },
                {
                    name: '医疗器械生产许可证',
                    key: 'prodLicense'
                },
                {
                    name: '其它',
                    key: 'other'
                }
            ],
            sourceName: ['平台', '医院', '经销商', '厂商'],
            previewVisible: false,
            previewImage: '',
            createDataObj: [],
            newEditDataObj: [],
            goodsImgList: [],
            certImgList: [],
            certObj: {
                begin_date: '',
                end_date: ''
            },
            mdrfImgList: [],
            mdrf_columns: [
                {
                    title: '注册号',
                    dataIndex: 'register_sn',
                    filed: 'register_sn',
                    align: 'center',
                    minWidth: '100',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    slots: {
                        default: ({ row }) => {
                            return [row.register_sn];
                        }
                    }
                },
                {
                    title: '开始日期',
                    dataIndex: 'begin_date',
                    filed: 'begin_date',
                    align: 'center',
                    minWidth: '100',
                    slots: {
                        default: ({ row }) => {
                            return [row.begin_date];
                        }
                    }
                },
                {
                    title: '截止日期',
                    dataIndex: 'end_date',
                    filed: 'end_date',
                    align: 'center',
                    minWidth: '100',
                    slots: {
                        default: ({ row }) => {
                            return [row.end_date];
                        }
                    }
                },
                {
                    title: '发证部门',
                    dataIndex: 'issue_organization',
                    filed: 'issue_organization',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    align: 'center',
                    minWidth: '100',
                    slots: {
                        default: ({ row }) => {
                            return [row.issue_organization];
                        }
                    }
                },
                {
                    title: '颁发日期',
                    dataIndex: 'issue_date',
                    filed: 'issue_date',
                    align: 'center',
                    minWidth: '100',
                    slots: {
                        default: ({ row }) => {
                            return [row.issue_date];
                        }
                    }
                }
            ],
            mdrfData: [],
            mdrfLoading: false,
            chooseTrMdrfObj: '',
            examImgList: [],
            bizLicenseImgList: [],
            prodLicenseImgList: [],
            otherImgList: [],
            userSystemType: parseFloat(this.$cookie.get('userSystemType')),
            spec_columns_test: [
                // 平台
                {
                    title: '产品编码',
                    dataIndex: 'specification_vendor_sn',
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '180px',
                    slots: {
                        default: ({ row }) => {
                            return [row.specification_vendor_sn];
                        }
                    }
                },
                {
                    title: '规格型号',
                    dataIndex: 'specification',
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '180px',
                    slots: {
                        default: ({ row }) => {
                            return [row.specification];
                        }
                    }
                },
                {
                    title: '包装单位',
                    dataIndex: 'common_use_unit',
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    width: '100px',
                    slots: {
                        default: ({ row }) => {
                            const value = this.$XEUtils.trim(row.common_use_unit) || '无';
                            return [value];
                        }
                    }
                },
                {
                    title: '状态',
                    dataIndex: 'status',
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    width: '80px',
                    slots: {
                        default: ({ row }) => {
                            let val;
                            let auditSt;
                            let text = row.status;
                            if (parseInt(text) === 1) {
                                val = '有效';
                                auditSt = 'color: #16a935';
                            }
                            if (parseInt(text) === 0) {
                                auditSt = 'color: red';
                                val = '无效';
                            }
                            return [<span style={auditSt}>{val}</span>];
                        }
                    }
                }
            ],
            spec_columns_vendor: [
                // 厂商
                {
                    title: '产品编码',
                    dataIndex: 'specification_vendor_sn',
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    width: '180',
                    slots: {
                        default: ({ row }) => {
                            return [row.specification_vendor_sn];
                        }
                    }
                },
                {
                    title: '规格型号',
                    dataIndex: 'specification',
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '180',
                    slots: {
                        default: ({ row }) => {
                            return [row.specification];
                        }
                    }
                },
                {
                    title: '医保编号',
                    dataIndex: 'default_health_care_sn',
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '180',
                    slots: {
                        default: ({ row }) => {
                            return [row.default_health_care_sn];
                        }
                    }
                },
                {
                    title: '阳光流水号',
                    dataIndex: 'default_sun_sn',
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '180',
                    slots: {
                        default: ({ row }) => {
                            return [row.default_sun_sn];
                        }
                    }
                },
                {
                    title: '包装单位',
                    dataIndex: 'common_use_unit',
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '100',
                    slots: {
                        default: ({ row }) => {
                            const value = this.$XEUtils.trim(row.common_use_unit) || '无';
                            return [value];
                        }
                    }
                },
                {
                    title: '状态',
                    dataIndex: 'status',
                    align: 'center',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    minWidth: '50',
                    slots: {
                        default: ({ row }) => {
                            let val;
                            let auditSt;
                            let text = row.status;
                            if (parseInt(text) === 1) {
                                val = '有效';
                                auditSt = 'color: #16a935';
                            }
                            if (parseInt(text) === 0) {
                                auditSt = 'color: red';
                                val = '无效';
                            }
                            return [<span style={auditSt}>{val}</span>];
                        }
                    }
                }
            ],
            specData: [],
            orgSpecData: [],
            specLoading: false,
            chooseTrSpecObj: {},
            // 产品[新增/编辑规格型号]模态框数据
            newSpecData: {
                spec: '',
                pvd: '',
                unit: '',
                healthCareSn: '',
                default_healthCareSn: '',
                sunSn: '',
                default_sunSn: ''
            },
            // [医保编号/阳光流水号]模态框数据
            healthAndSunSnModal: {
                title: '',
                visible: false,
                health_care_columns: [
                    {
                        title: '医保编号',
                        dataIndex: 'health_care_sn',
                        align: 'center',
                        slots: {
                            default: ({ row }) => {
                                return [row.sn];
                            }
                        }
                    },
                    {
                        title: '默认',
                        dataIndex: 'is_default',
                        align: 'center',
                        slots: {
                            default: ({ row }) => {
                                return [row.is_default ? '是' : '否'];
                            }
                        }
                    }
                ],
                sun_columns: [
                    {
                        title: '阳光流水号',
                        dataIndex: 'sun_sn',
                        align: 'center',
                        slots: {
                            default: ({ row }) => {
                                return [row.sn];
                            }
                        }
                    },
                    {
                        title: '默认',
                        dataIndex: 'is_default',
                        align: 'center',
                        slots: {
                            default: ({ row }) => {
                                return [row.is_default ? '是' : '否'];
                            }
                        }
                    }
                ],
                // API获取编号数据
                data: []
            },
            // 医保编号/阳光流水号的新增/编辑
            healthAndSunAddModal: {
                isEdit: false, // true-编辑 false-新增
                title: '', // 模态框标题
                visible: false, // 模态框可见
                input_sn: '', // 用户输入的编号
                is_default: false, // 用户勾选的默认
                objSelected: false, // 编号表中是否有选中
                // 编号表中当前选中行对象
                currentObj: {
                    index: 0,
                    is_default: false,
                    product_inner_sn: 0,
                    sn: '',
                    sn_inner_sn: 0,
                    specification_inner_sn: 0,
                    type: '',
                    vendor_inner_sn: 0
                }
            },
            isHealthCareModal: false, // 是否是医保编号窗口
            isEdit: false,
            packUnitObj: {},
            switchType: true,
            otherCertImgs: [], // 针对于打印的，“营业执照”和“医疗器械生产许可证”的图片数组，从entry-gic组件中回传回来
            sync: {
                isSync: false,
                visible: false,
                shouldSync: true
            }
        };
    },
    computed: {
        scrollYNumber() {
            let clientHeight = document.documentElement.clientHeight || document.body.clientHeight,
                pager = 115;
            return (clientHeight - pager) / 2;
        }
    },
    methods: {
        handleSearchSpecification() {
            // 规格型号区域 => 产品编码/规格型号搜索
            const value = this.specificationSearchVal;
            if (!value) {
                this.specData = this.orgSpecData;
                return;
            }
            const list = this.orgSpecData.filter((item) => {
                const specificationSn = item.specification_vendor_sn || '';
                const specification = item.specification || '';
                return specificationSn.indexOf(value) !== -1 || specification.indexOf(value) !== -1;
            });
            this.specData = list;
        },
        dragControllerDiv: function () {
            let that = this;
            let resize = document.getElementsByClassName('resize');
            let boxtop = document.getElementsByClassName('boxtop');
            let mid = document.getElementsByClassName('boxdown');
            let box = document.getElementsByClassName('box');
            for (let i = 0; i < resize.length; i++) {
                // 鼠标按下事件
                resize[i].onmousedown = function (e) {
                    let startY = e.clientY;
                    resize[i].top = resize[i].offsetTop;
                    // 鼠标拖动事件
                    document.onmousemove = function (e) {
                        let endY = e.clientY;
                        let moveLen = resize[i].top + (endY - startY); // （endx-startx）=移动的距离。resize[i].left+移动的距离=左边区域最后的宽度
                        let maxT = box[i].clientHeight - resize[i].offsetHeight;
                        if (moveLen < 200) moveLen = 200; // 左边区域的最小宽度为200px
                        if (moveLen > maxT - 200) moveLen = maxT - 200; // 右边区域最小宽度为200px
                        resize[i].style.top = moveLen; // 设置左侧区域的宽度
                        that.boxTopHeight = moveLen - 56;
                        that.boxDownHeight = box[i].clientHeight - moveLen - 18;
                        for (let j = 0; j < boxtop.length; j++) {
                            boxtop[j].style.height = moveLen + 'px';
                            mid[j].style.height = box[i].clientHeight - moveLen - 18 + 'px';
                        }
                    };
                    // 鼠标松开事件
                    document.onmouseup = function () {
                        document.onmousemove = null;
                        document.onmouseup = null;
                        resize[i].releaseCapture && resize[i].releaseCapture();
                    };
                    resize[i].setCapture && resize[i].setCapture();
                    return false;
                };
            }
        },
        dragControllerDivHoriz: function () {
            let that = this;
            let res = document.getElementsByClassName('resizeHoriz');
            let boxtop = document.getElementsByClassName('spec-box table-part');
            let mid = document.getElementsByClassName('tab-table-wrap minimal more2');
            let box = document.getElementsByClassName('bottom-part more');
            for (let i = 0; i < res.length; i++) {
                // 鼠标按下事件
                res[i].onmousedown = function (e) {
                    let startX = e.clientX;
                    res[i].left = res[i].offsetLeft;
                    // 鼠标拖动事件
                    document.onmousemove = function (e) {
                        let endX = e.clientX;
                        let moveLen = res[i].left + (endX - startX); // （endx-startx）=移动的距离。resize[i].left+移动的距离=左边区域最后的宽度
                        let maxT = box[i].clientWidth - res[i].offsetWidth;
                        if (moveLen < 480) moveLen = 480; // 左边区域的最小宽度为200px
                        if (moveLen > maxT - 350) moveLen = maxT - 350; // 右边区域最小宽度为200px
                        res[i].style.left = moveLen; // 设置左侧区域的宽度
                        that.boxLeftWidth = moveLen - 56;
                        that.boxRightWidth = box[i].clientWidth - moveLen - 18;
                        for (let j = 0; j < boxtop.length; j++) {
                            boxtop[j].style.width = moveLen + 'px';
                            mid[j].style.width = box[i].clientWidth - moveLen - 18 + 'px';
                        }
                    };
                    // 鼠标松开事件
                    document.onmouseup = function () {
                        document.onmousemove = null;
                        document.onmouseup = null;
                        res[i].releaseCapture && res[i].releaseCapture();
                    };
                    res[i].setCapture && res[i].setCapture();
                    return false;
                };
            }
        },
        openChooseTb() {
            // 打开选择厂商
            this.chooseOrgMd.alert = true;
            if (this.$refs.chooseOrgMd) {
                this.$refs.chooseOrgMd.pageDataInit();
            }
        },
        clearSearchDataVenFn() {
            // 清空选择
            this.vendorObj.name = '';
            this.searchData.vendor_inner_sn = '';
        },
        async chooseOrgFn(org) {
            // 获取选中机构信息
            this.vendorObj.name = org.org_name;
            this.searchData.vendor_inner_sn = org.org_id;
            this.chooseOrgMd.alert = false;
        },
        async getVendorListFn() {
            // 获取厂商
            await getVendorList().then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.vendorList = res.list;
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        changeVendor(val) {
            // 选择厂商
            this.searchData.vendor_inner_sn = val;
        },
        async getCategoryListFn() {
            // 获取自定义类型列表
            await categoryGetParentListAPI().then((res) => {
                if (parseFloat(res.code) === 0) {
                    const rows = res.list;
                    let newArray = [];
                    newArray = rows.map((item) => {
                        let st;
                        if (item.is_leaf === 1) {
                            st = true;
                        } else if (item.is_leaf === 0) {
                            st = false;
                        }
                        item.label = item.type;
                        item.value = item.type_inner_sn;
                        item.isLeaf = st;
                        return item;
                    });
                    this.categoryList = newArray;
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        async loadCategory(options) {
            // 加载自定义类型列表子集
            const targetOption = options[options.length - 1];
            targetOption.loading = true;
            const id = targetOption.value;
            await categoryGetNextListAPI(id).then((res) => {
                targetOption.loading = false;
                if (parseFloat(res.code) === 0) {
                    const rows = res.list;
                    if (rows.length > 0) {
                        targetOption.children = rows.map((item) => {
                            let st;
                            if (item.is_leaf === 1) {
                                st = true;
                            } else if (item.is_leaf === 0) {
                                st = false;
                            }
                            item.label = item.type;
                            item.value = item.type_inner_sn;
                            item.isLeaf = st;
                            return item;
                        });
                    } else {
                        targetOption.isLeaf = true;
                    }
                    targetOption.loading = false;
                    this.categoryList = [...this.categoryList];
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        async get68ParentListFn() {
            // 获取68分类
            await code68ParentListAPI().then((res) => {
                if (parseFloat(res.code) === 0) {
                    const rows = res.list;
                    let newArray = [];
                    rows.forEach((item) => {
                        let st;
                        if (item.is_leaf === 1) {
                            st = true;
                        } else if (item.is_leaf === 0) {
                            st = false;
                        }
                        let obj = {
                            label: '(' + item.code68_sn + ') ' + item.name,
                            value: item.code68_sn,
                            isLeaf: st
                        };
                        newArray.push(obj);
                    });
                    this.op68List = newArray;
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        async load68data(options) {
            // 加载68分
            const targetOption = options[options.length - 1];
            targetOption.loading = true;
            const id = targetOption.value;
            await code68ChildrenListAPI(id).then((res) => {
                targetOption.loading = false;
                if (parseFloat(res.code) === 0) {
                    const rows = res.list;
                    if (rows.length > 0) {
                        targetOption.children = rows.map((item) => {
                            let st;
                            if (item.is_leaf === 1) {
                                st = true;
                            } else if (item.is_leaf === 0) {
                                st = false;
                            }
                            item.label = '(' + item.code68_sn + ') ' + item.name;
                            item.value = item.code68_sn;
                            item.isLeaf = st;
                            return item;
                        });
                    } else {
                        targetOption.isLeaf = true;
                    }
                    targetOption.loading = false;
                    this.op68List = [...this.op68List];
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        displayRender(obj) {
            let len = obj.labels.length;
            const label = obj.labels[len - 1];
            return label;
        },
        async getUploadTokenAPIFn() {
            // 获取上传 token
            await getUploadTokenAPI().then((res) => {
                const { code, data } = res;
                if (parseFloat(code) === 0) {
                    this.upToken = data;
                    this.upLoadparams = {
                        token: this.upToken
                    };
                } else {
                    this.upToken = '';
                    this.$message.error(res.msg);
                }
            });
        },
        getCurrentStyle(current) {
            const style = {};
            if (current.date() === 1) {
                style.border = '1px solid #1890ff';
                style.borderRadius = '50%';
            }
            return style;
        },
        // 单击行
        cellClickFn: clickDebounce.debounce(function (row) {
            let obj = row.row || row;
            if (this.chooseTrObj === obj) return;
            this.chooseTrEvent(obj);
        }),
        // 双击行
        cellDblclickFn({ row }) {
            const timer = clickDebounce.getTimer();
            if (timer) {
                // 清除单击事件 防止和双击事件同时触发
                clickDebounce.clearTimer();
            }
            this.chooseTrEvent(row);
            this.openEdit();
        },
        chooseTrEvent(record) {
            // 选中事件
            this.chooseTrObj = record;
            if (record.status === 1) {
                this.sync.shouldSync = true;
            } else {
                this.sync.shouldSync = false;
            }
            this.isChooseTr = true;
            this.chooseTrSpecObj = {};
            // this.chooseTr(record.vendor_inner_sn, record.product_inner_sn)
            let typeInnerSn = this.chooseTrObj.type_inner_sn;
            if (typeInnerSn && typeInnerSn.length > 0) {
                let index = this.chooseTrObj.type_inner_sn.length - 1;
                this.chooseTrObj.type_inner_sn = this.chooseTrObj.type_inner_sn[index];
            }
            let code68Sn = this.chooseTrObj.code68_sn;
            if (code68Sn && code68Sn.length > 0) {
                let index = this.chooseTrObj.code68_sn.length - 1;
                this.chooseTrObj.code68_sn = this.chooseTrObj.code68_sn[index];
            }
            setTimeout(() => {
                this.$refs.xTable.setCurrentRow(this.chooseTrObj); // 默认选中
            }, 300);
            this.getGoodsImg();
            this.getCertImg();
            this.getGicMdrf();
            this.getExamImg();
            this.getBizLicenseImg();
            this.getProdLicenseImg();
            this.getOtherImg();
            this.loadSpecFn();
        },
        chooseTr(vid, pid) {
            // 选中行渲染
            this.isChooseTr = true;
            this.tableData.forEach((item) => {
                if (item.vendor_inner_sn === vid && item.product_inner_sn === pid) {
                    item.isActive = 'active';
                    this.chooseTrObj = item;
                } else {
                    item.isActive = '';
                }
            });
        },
        getCpmData(data) {
            this.createDataObj = data;
        },
        getEditData(data) {
            this.newEditDataObj = JSON.parse(JSON.stringify(data));
        },
        getUpGicData(data) {
            this.uploadGicData = data;
        },
        getMdrfGicData(data) {
            this.newMdrfGicData = JSON.parse(JSON.stringify(data));
        },
        async saveGoodsFn() {
            // 保存
            this.$refs.createPage.returnPageData();
            let dataObj = this.createDataObj;
            let typeInnerSn = dataObj.type_inner_sn;
            if (typeInnerSn.length > 0) {
                let index = dataObj.type_inner_sn.length - 1;
                dataObj.type_inner_sn = dataObj.type_inner_sn[index];
            }
            let code68Sn = dataObj.code68_sn;
            if (code68Sn.length > 0) {
                let index = dataObj.code68_sn.length - 1;
                dataObj.code68_sn = dataObj.code68_sn[index];
            }

            if (dataObj.vendor_inner_sn.length === 0 || dataObj.vendor_inner_sn === undefined) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择厂商',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            if (dataObj.name === '' || !dataObj.name) {
                this.$warning({
                    title: '操作提示',
                    content: '请输入名称',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            await platformProductAdd(dataObj).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.createSt = false;
                    this.loadTable(res.info);
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 编辑
        editGoodsFn() {
            this.$refs.editPage.returnPageData();
            let oldData = this.chooseTrObj;
            let newData = this.newEditDataObj;
            let finalData = {};
            let editInfoFlag = true;
            // 获取68 和 type
            let typeInnerSn = newData.type_inner_sn;
            if (typeInnerSn.length > 0) {
                let index = newData.type_inner_sn.length - 1;
                newData.type_inner_sn = newData.type_inner_sn[index];
            } else {
                newData.type_inner_sn = null;
            }
            let code68Sn = newData.code68_sn;
            if (code68Sn.length > 0) {
                let index = newData.code68_sn.length - 1;
                newData.code68_sn = newData.code68_sn[index];
            } else {
                newData.code68_sn = null;
            }

            for (let server in newData) {
                let serverItem = newData[server];
                for (let old in oldData) {
                    let oldItem = oldData[old];
                    if (server === old && serverItem !== oldItem) {
                        finalData = {
                            ...finalData,
                            [server]: serverItem, // 新值
                            [server + '_old']: oldItem // 旧值
                        };
                        editInfoFlag = false;
                    }
                }
            }
            if (newData.vendor_inner_sn.length === 0 || newData.vendor_inner_sn === undefined) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择厂商',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            newData.name = this.$Utils.trim(newData.name);
            if (newData.name === '' || !newData.name) {
                this.$warning({
                    title: '操作提示',
                    content: '请输入名称',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            if (!editInfoFlag) {
                this.editProServe(finalData);
            } else {
                this.editSt = false;
            }
        },
        async editProServe(data) {
            let vid = this.chooseTrObj.vendor_inner_sn;
            let pid = this.chooseTrObj.product_inner_sn;
            await platformProductEditAPI(vid, pid, data).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.editSt = false;
                    data.vendor_inner_sn = this.chooseTrObj.vendor_inner_sn;
                    data.product_inner_sn = this.chooseTrObj.product_inner_sn;
                    this.loadTable(data);
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 删除
        deleteBtn() {
            if (!this.chooseTrObj.vendor_inner_sn || !this.chooseTrObj.product_inner_sn) {
                this.$warning({
                    title: '操作提示',
                    content: this.$Utils.tips().noSelected,
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            const _this = this;
            _this.$confirm({
                title: '删除',
                content: '您真要执行该操作吗？',
                centered: true,
                maskClosable: false,
                onOk() {
                    _this.deleteProduct();
                }
            });
        },
        async deleteProduct() {
            let vid = this.chooseTrObj.vendor_inner_sn;
            let pid = this.chooseTrObj.product_inner_sn;
            await platformProductDelAPI(vid, pid).then((res) => {
                if (parseFloat(res.code) === 0) {
                    // this.tableData.forEach((item, index) => {
                    //     if (item.vendor_inner_sn === vid && item.product_inner_sn === pid) {
                    //         this.tableData.splice(index, 1)
                    //     }
                    // })
                    this.chooseTrObj = {};
                    this.isChooseTr = false;
                    this.loadTable();
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        initPage() {
            this.pageInit.current = 1;
            this.loadTable();
        },
        async loadTable(chooseObj) {
            if (this.$Utils.checkInputValFn(this.searchData.name)) {
                this.$warning({
                    title: '操作提示',
                    content: `${this.$Utils.tips().specialChart}`,
                    okText: '知道了',
                    centered: true,
                    maskClosable: false
                });
                this.pageInit.loading = false;
                return false;
            }
            this.pageInit.loading = true;
            let params = JSON.parse(JSON.stringify(this.searchData));
            if (params.type_inner_sn.length > 0 && this.highSearch) {
                params.type_inner_sn = params.type_inner_sn[params.type_inner_sn.length - 1];
            } else {
                params.type_inner_sn = '';
            }
            if (params.code68_sn.length > 0 && this.highSearch) {
                params.code68_sn = params.code68_sn[params.code68_sn.length - 1];
            } else {
                params.code68_sn = '';
            }
            if (!this.highSearch) {
                params.vendor_inner_sn = '';
            }
            if (parseInt(this.nowSystemType) === 3) {
                params.vendor_inner_sn = this.nowOid;
            }
            params.pageSize = this.pageInit.pageSize;
            params.pageNum = this.pageInit.current;
            params.org_id = this.$cookie.get('userbelong');

            // 排序
            params.orderByColumn = 'product_inner_sn';
            params.isAsc = 'desc';

            await platformProductPublicList({ params }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.pageInit.loading = false;
                    if (res.rows.length === 0) {
                        this.isChooseTr = false;
                        this.chooseTrObj = {};
                    }
                    this.tableData = res.rows.map((item) => {
                        item.isActive = '';
                        item.key = item.vendor_inner_sn + '' + item.product_inner_sn;
                        if (item.code68_name) {
                            item.text_68 = item.code68_name + '(' + item.code68_sn + ')';
                        }
                        item.sourceName = this.sourceName[parseFloat(item.data_source)];
                        return item;
                    });
                    let total = res.total;
                    this.pageInit = {
                        ...this.pageInit,
                        total
                    };
                    // this.chooseTrObj = {}
                    // 清除缓存，等待新的数据返回

                    if (this.tableData.length > 0) {
                        if (chooseObj) {
                            // 选中新增/编辑的项
                            this.tableData.forEach((item) => {
                                if (item.key === this.chooseTrObj.key) {
                                    this.$refs.xTable.setCurrentRow(item); // 默认选中
                                    this.cellClickFn(item);
                                }
                            });
                        } else {
                            this.$refs.xTable.setCurrentRow(this.tableData[0]); // 默认选中第一
                            this.cellClickFn(this.tableData[0]);
                        }
                    } else {
                        this.specData = [];
                        this.goodsImgList = [];
                        this.certImgList = [];
                        this.mdrfImgList = [];
                        this.examImgList = [];
                        this.bizLicenseImgList = [];
                        this.prodLicenseImgList = [];
                        this.mdrfData = [];
                    }
                    this.$refs.xTable.refreshColumn(); // 防止错位
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        changePaginationFn({ currentPage, pageSize }) {
            this.pageInit.current = currentPage;
            this.pageInit.pageSize = pageSize;
            this.pageInit.loading = true;
            this.loadTable();
            this.isChooseTr = false;
        },
        openCreate() {
            this.createSt = true;
            this.openType = 'create';
            let cmp = this.$refs.createPage;
            if (cmp) {
                cmp.initPageData();
            }
            this.modalData = [];
            this.$Utils.globalDragCenterFn('create');
        },
        openEdit() {
            // 打开修改
            if (this.chooseTrObj.product_inner_sn) {
                // 赋值
                this.editSt = true;
                this._setModelCenter('editModel');
                if (this.$refs.editPage) {
                    this.sendEditInfo();
                }
            } else {
                this.$warning({
                    title: '操作提示',
                    content: this.$Utils.tips().noSelected,
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
            }
        },
        _setModelCenter(className) {
            this.$nextTick(() => {
                let clientWidth = document.documentElement.clientWidth || document.body.clientWidth;
                let clientHeight = document.documentElement.clientHeight || document.body.clientHeight;
                let modalEl = document.getElementsByClassName(className)[0];
                if (modalEl) {
                    modalEl.style.left = (clientWidth - modalEl.offsetWidth) / 2 + 'px';
                    modalEl.style.top = (clientHeight - modalEl.offsetHeight) / 2 + 'px';
                }
            });
        },
        sendEditInfo() {
            // 加载编辑
            this.editTrObj.vid = this.chooseTrObj.vendor_inner_sn;
            this.editTrObj.pid = this.chooseTrObj.product_inner_sn;
            this.editTrObj.vendor_name = this.chooseTrObj.vendor_name;
            this.editTrObj.vendor_inner_sn_old = this.chooseTrObj.vendor_inner_sn;
            this.editTrObj.type_inner_sn_old = this.chooseTrObj.type_inner_sn;
            this.editTrObj.code68_sn_old = this.chooseTrObj.code68_sn;
            this.editTrObj.storage_condition_old = this.chooseTrObj.storage_condition;
            this.editTrObj.health_care_type_old = this.chooseTrObj.health_care_type;
            this.editTrObj.name_old = this.chooseTrObj.name;
            this.editTrObj.specifications_old = this.chooseTrObj.specifications;
            this.editTrObj.health_care_sn_old = this.chooseTrObj.health_care_sn;
            this.editTrObj.description_old = this.chooseTrObj.description;
            this.editTrObj.type_old = this.chooseTrObj.type;
            this.editTrObj.status_old = this.chooseTrObj.status;
            this.editTrObj.code68_type_old = this.chooseTrObj.code68_type;
            this.$refs.editPage.initEditData();
        },
        deleteImg(obj) {
            // 删除图片
            const _this = this;
            _this.$confirm({
                title: '删除',
                content: '您真要执行该操作吗？',
                centered: true,
                maskClosable: false,
                onOk() {
                    _this.delImgFn(obj);
                }
            });
        },
        async delImgFn(obj) {
            // 删除图片
            await platformProductImgDelAPI(
                obj.vendor_inner_sn,
                obj.product_inner_sn,
                obj.type,
                obj.image_inner_sn
            ).then((res) => {
                if (parseInt(res.code) === 0) {
                    this.getGoodsImg();
                    this.getExamImg();
                    this.getBizLicenseImg();
                    this.getProdLicenseImg();
                    this.getOtherImg();
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        previewImg(src) {
            // 图片预览
            let that = this;
            if (src && src !== '') {
                let img = new Image();
                img.src = src;
                img.onload = function () {
                    that.previewWid = img.width + 50;
                    let orderWidth = document.body.clientWidth; // 最大不能超过屏幕宽度
                    if (that.previewWid > orderWidth) {
                        that.previewWid = orderWidth - 246;
                    }
                    if (that.previewWid < 300) {
                        that.previewWid = 300;
                    }
                    that.preImgSt = true;
                    that.imgSrc = src;
                    that.$Utils.globalDragCenterFn('imgModal');
                };
            }
        },
        closePreview() {
            this.preImgSt = false;
        },
        handleCancel() {
            this.previewVisible = false;
        },
        handlePreview(file) {
            this.previewImage = file.url || file.thumbUrl;
            this.previewVisible = true;
        },
        checkFile(file) {
            this.getUploadTokenAPIFn();
            let fileName = this.$Utils.formatFileNameToLowerCase(file.name);
            let index1 = fileName.lastIndexOf('.');
            let index2 = fileName.length;
            let postName = fileName.substring(index1 + 1, index2); // 后缀名
            if (
                postName !== 'png' &&
                postName !== 'jpeg' &&
                postName !== 'jpg' &&
                postName !== 'PNG' &&
                postName !== 'JPEG' &&
                postName !== 'JPG'
            ) {
                this.$message.error('仅支持png / jpeg 格式图片文件');
                return false;
            }
        },
        async upGoodsImg(info) {
            // 上传商品图片
            info.file.name = this.$Utils.formatFileNameToLowerCase(info.file.name);

            if (info.file.status === 'done') {
                const res = info.file.response;
                if (res.Code || res.code === 0) {
                    let url = res.data.key;
                    let params = {
                        vendor_inner_sn: this.chooseTrObj.vendor_inner_sn,
                        product_inner_sn: this.chooseTrObj.product_inner_sn,
                        type: 1,
                        url: url
                    };
                    await platformProductImgAddAPI(params).then((res) => {
                        if (parseFloat(res.code) === 0) {
                            this.goodsImgList.push({ ...res.info });
                        } else {
                            this.$message.error(res.msg);
                        }
                    });
                } else {
                    this.$message.error(res.msg);
                }
            }
        },
        async upOtherImg(info) {
            // 上传其他图片
            info.file.name = this.$Utils.formatFileNameToLowerCase(info.file.name);

            if (info.file.status === 'done') {
                const res = info.file.response;
                if (res.Code || res.code === 0) {
                    let url = res.data.key;
                    let params = {
                        vendor_inner_sn: this.chooseTrObj.vendor_inner_sn,
                        product_inner_sn: this.chooseTrObj.product_inner_sn,
                        type: 0,
                        url: url
                    };
                    await platformProductImgAddAPI(params).then((res) => {
                        if (parseFloat(res.code) === 0) {
                            this.otherImgList.push({ ...res.info });
                        } else {
                            this.$message.error(res.msg);
                        }
                    });
                } else {
                    this.$message.error(res.msg);
                }
            }
        },
        /**
         * 获取产品图片列表
         */
        async getGoodsImg() {
            let vid = this.chooseTrObj.vendor_inner_sn;
            let pid = this.chooseTrObj.product_inner_sn;
            await platformProductImgGetAPI(vid, pid, 1).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.goodsImgList = res.images;
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        /**
         * 获取质量检测报告图片列表
         */
        async getExamImg() {
            let vid = this.chooseTrObj.vendor_inner_sn;
            let pid = this.chooseTrObj.product_inner_sn;
            await platformProductImgGetAPI(vid, pid, 4).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.examImgList = res.images;
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        /**
         * 获取营业执照图片列表
         */
        async getBizLicenseImg() {
            const vid = this.chooseTrObj.vendor_inner_sn;
            await platformOrgImgListAPI(3, vid, 1).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.bizLicenseImgList = res.list;
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        /**
         * 获取医疗器械生产许可证图片列表
         */
        async getProdLicenseImg() {
            const vid = this.chooseTrObj.vendor_inner_sn;
            await platformOrgImgListAPI(3, vid, 2).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.prodLicenseImgList = res.list;
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        async getOtherImg() {
            let vid = this.chooseTrObj.vendor_inner_sn;
            let pid = this.chooseTrObj.product_inner_sn;
            await platformProductImgGetAPI(vid, pid, 0).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.otherImgList = res.images;
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        /** 注册证方法区 **/
        async getCertImg() {
            // 获取注册证图片
            let vid = this.chooseTrObj.vendor_inner_sn;
            let pid = this.chooseTrObj.product_inner_sn;
            await platformProductImgGetAPI(vid, pid, 2).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.certImgList = res.images;
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        async getGicMdrf() {
            // 获取注册证登记表
            this.mdrfLoading = true;
            this.mdrfData = [];
            let vid = this.chooseTrObj.vendor_inner_sn;
            let pid = this.chooseTrObj.product_inner_sn;
            await platformProductMdrfGet(vid, pid).then((res) => {
                if (parseFloat(res.code) === 0) {
                    if (parseFloat(res.code) === 0) {
                        this.mdrfData = res.list.map((item) => {
                            item.key = item.mdrf_inner_sn;
                            item.isActive = '';
                            return item;
                        });
                        if (this.$refs.mdrfTable && this.$refs.mdrfTable.length > 0) {
                            this.$refs.mdrfTable[0].refreshColumn();
                        }
                    } else {
                        this.$message.error(res.msg);
                    }
                    this.gicMdrfObj.name = res.info.product_name;
                    this.gicMdrfObj.vendor_name = res.info.vendor_name;
                    this.gicMdrfObj.address = res.info.vendor_address;
                    this.gicMdrfObj.specifications = this.chooseTrObj.specifications;
                    if (this.chooseTrMdrfObj !== '') {
                        this.mdrfData.forEach((item) => {
                            if (item.mdrf_inner_sn === this.chooseTrMdrfObj.mdrf_inner_sn) {
                                item.isActive = 'active-tr';
                                this.$refs.mdrfTable[0].setCurrentRow(item);
                            } else {
                                item.isActive = '';
                            }
                        });
                    }
                    if (!this.mdrfData.length) {
                        this.chooseTrMdrfObj = '';
                    }
                } else {
                    this.$message.error(res.msg);
                }
                this.mdrfLoading = false;
            });
        },
        openGicMdrf() {
            // 打开注册证登记表录入
            this.gicMdrfObj.new = true;
            this.gicMdrfObj.vendor_inner_sn = this.chooseTrObj.vendor_inner_sn;
            this.mdrfIsNew = true;
            this.entrySt = true;
            if (this.$refs.mdrfGicPage) {
                this.$Utils.globalDragCenterFn('entryModal');
                this.$refs.mdrfGicPage.refreshPage();
            } else {
                this.$Utils.globalDragCenterFn('entryModal', true);
            }
        },
        async editGicMdrf() {
            // 修改注册证
            this.mdrfIsNew = false;
            if (!this.chooseTrMdrfObj || this.chooseTrMdrfObj === '') {
                this.$warning({
                    title: '操作提示',
                    content: '请选择注册证',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return;
            }
            let vid = this.chooseTrMdrfObj.vendor_inner_sn;
            let pid = this.chooseTrMdrfObj.product_inner_sn;
            let mid = this.chooseTrMdrfObj.mdrf_inner_sn;
            await platformProductMdrfInfo(vid, pid, mid).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.gicMdrfObj = {
                        ...res.info,
                        vendor_inner_sn: vid,
                        new: false
                    };
                    this.oldMdrfObj = JSON.parse(JSON.stringify(res.info));
                } else {
                    this.$message.error(res.msg);
                }
            });
            this.entrySt = true;
            if (this.$refs.mdrfGicPage) {
                this.$Utils.globalDragCenterFn('entryModal');
                this.$refs.mdrfGicPage.refreshPage();
            } else {
                this.$Utils.globalDragCenterFn('entryModal', true);
            }
        },
        async entryGicMdrf() {
            // 录入注册证登记表
            this.$refs.mdrfGicPage.returnPageData();
            let vid = this.chooseTrObj.vendor_inner_sn;
            let pid = this.chooseTrObj.product_inner_sn;
            if (this.mdrfIsNew) {
                // 新增
                let data = this.newMdrfGicData;
                data.register_sn = this.$Utils.trim(data.register_sn);
                if (!data.register_sn || data.register_sn === '') {
                    this.$warning({
                        title: '操作提示',
                        content: '注册号不能为空',
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    return;
                }
                if (!data.begin_date || data.begin_date === '') {
                    this.$warning({
                        title: '操作提示',
                        content: '有效期不能为空',
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    return false;
                }
                data.product_name = this.$Utils.trim(data.product_name);
                if (!data.product_name || data.product_name === '') {
                    this.$warning({
                        title: '操作提示',
                        content: '产品名称不能为空',
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    return;
                }
                if (!data.issue_date) {
                    this.$warning({
                        title: '操作提示',
                        content: '发证日期不能为空',
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    return false;
                }
                data.product_name_old = this.gicMdrfObj.name;
                data.specifications_old = this.gicMdrfObj.specifications;
                await platformProductMdrfAdd(vid, pid, data).then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.entrySt = false;
                        this.chooseTrMdrfObj = res.info;
                        this.getGicMdrf();
                    } else {
                        this.$message.error(res.msg);
                    }
                });
            } else {
                // 修改
                let oldData = JSON.parse(JSON.stringify(this.oldMdrfObj));
                let newData = JSON.parse(JSON.stringify(this.newMdrfGicData));
                let baseInfoChanged = false;
                // let imgListNew = newData.images
                // let imgListOld = oldData.images
                // oldData.images = ''
                // newData.images = ''
                let finalData = {};
                for (let server in newData) {
                    let serverItem = newData[server];
                    for (let old in oldData) {
                        let oldItem = oldData[old];
                        if (server === old && serverItem !== oldItem) {
                            finalData = {
                                ...finalData,
                                [server]: serverItem, // 新值
                                [server + '_old']: oldItem // 旧值
                            };
                            baseInfoChanged = true;
                        }
                    }
                }

                finalData.images = [...newData.images]; // === 后端约定，不管改没改变注册证图片，都必须传递
                // 比较图片数组是否有修改操作  没有改动就不做修改提交
                // finalData.images = [...oldData.images, ...newData.images]
                // let a = true
                // if (a) return
                // let imgListChanged = false
                // if (!this.$XEUtils.isEqual(imgListOld, imgListNew)) {
                //     finalData.images = imgListNew
                //     imgListChanged = true
                // } else {
                //     imgListChanged = false
                // }

                const changed = baseInfoChanged;

                newData.register_sn = this.$Utils.trim(newData.register_sn);
                if (!newData.register_sn || newData.register_sn === '') {
                    this.$warning({
                        title: '操作提示',
                        content: '注册号不能为空',
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    return false;
                }
                if (!newData.begin_date || newData.begin_date === '') {
                    this.$warning({
                        title: '操作提示',
                        content: '有效期不能为空',
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    return false;
                }
                newData.product_name = this.$Utils.trim(newData.product_name);
                if (!newData.product_name || newData.product_name === '') {
                    this.$warning({
                        title: '操作提示',
                        content: '产品名称不能为空',
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    return;
                }
                if (!newData.issue_date) {
                    this.$warning({
                        title: '操作提示',
                        content: '发证日期不能为空',
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    return;
                }
                let mid = this.chooseTrMdrfObj.mdrf_inner_sn;
                if (changed) {
                    await platformProductMdrfEdit(vid, pid, mid, finalData).then((res) => {
                        if (parseFloat(res.code) === 0) {
                            this.entrySt = false;
                            this.getGicMdrf();
                        } else {
                            this.$message.error(res.msg);
                        }
                    });
                } else {
                    this.entrySt = false;
                }
            }
        },
        async delGicMdrf() {
            // 删除注册证件
            if (!this.chooseTrMdrfObj || this.chooseTrMdrfObj === '') {
                this.$warning({
                    title: '操作提示',
                    content: '请选择注册证',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return;
            }
            let vid = this.chooseTrObj.vendor_inner_sn;
            let pid = this.chooseTrObj.product_inner_sn;
            let mid = this.chooseTrMdrfObj.mdrf_inner_sn;
            await platformProductMdrfDel(vid, pid, mid).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.chooseTrMdrfObj = '';
                    this.getGicMdrf();
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        mdrfClickFn(row) {
            this.chooseTrMdrfObj = row.row || row;
        },
        mrdfDblclickFn(row) {
            this.chooseTrMdrfObj = row.row || row;
            this.editGicMdrf();
        },
        /** 注册证方法区end **/
        printFn() {
            // 打印
            let printContent = {
                printData: this.$refs.printData.innerHTML,
                printType: 1 // printType  1、图片；2、耗材申领；3、出入库单; 4、询价单
            };
            printContent = JSON.stringify(printContent);
            window.localStorage.printContent = printContent;
            let routeUrl = this.$router.resolve({
                path: 'print',
                name: 'print'
            });
            window.open(routeUrl.href, '_blank');
        },
        async loadSpecFn(chooseObj) {
            const { vendor_inner_sn: vendorInnerSn, product_inner_sn: productInnerSn } = this.chooseTrObj;
            this.specLoading = true;
            this.specData = [];
            await platformProductSpecList(vendorInnerSn, productInnerSn)
                .then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.specData = res.list.map((item) => ({
                            ...item,
                            isActive: '',
                            key: item.specification_inner_sn
                        }));
                        // 数组拷贝，保持两份数据的独立互不干扰
                        this.orgSpecData = JSON.parse(JSON.stringify(this.specData));

                        // 默认选中表格中第一条数据
                        this.$nextTick(() => {
                            const specTable = this.$refs.specTable;
                            if (this.specData && this.specData.length) {
                                this.chooseTrSpecObj = this.specData[0];
                                specTable.setCurrentRow(this.specData[0]);
                            } else {
                                specTable.clearCurrentRow();
                                this.chooseTrSpecObj = {};
                            }

                            if (chooseObj) {
                                this.chooseTrSpecObj = chooseObj;
                            }
                            this.packUnitObj = {
                                vid: vendorInnerSn,
                                pid: productInnerSn,
                                psid: this.chooseTrSpecObj.specification_inner_sn
                            };
                            this.chooseTrSpec(this.chooseTrSpecObj.specification_inner_sn);
                        });
                    } else {
                        this.$message.error(res.msg);
                    }
                })
                .finally(() => {
                    this.specLoading = false;
                });
        },
        specClickFn(row) {
            this.chooseTrSpecObj = row.row || row;
            let vid = this.chooseTrObj.dealer_inner_sn || this.chooseTrObj.vendor_inner_sn;
            let pid = this.chooseTrObj.product_inner_sn;
            let id = this.chooseTrSpecObj.specification_inner_sn;
            let infoData = {
                vid: vid,
                pid: pid,
                psid: id
            };
            this.packUnitObj = infoData;
        },
        specDblclickFn(row) {
            this.chooseTrMdrfObj = row.row || row;
            this.openEditSpec();
        },
        customRowSpec(record) {
            // 自定义行
            return {
                class: record.isActive,
                on: {
                    // 事件
                    click: () => {
                        // 点击行
                        this.chooseTrSpecObj = record;
                        let vid = this.chooseTrObj.vendor_inner_sn;
                        let pid = this.chooseTrObj.product_inner_sn;
                        let id = this.chooseTrSpecObj.specification_inner_sn;
                        let infoData = {
                            vid: vid,
                            pid: pid,
                            psid: id
                        };
                        this.packUnitObj = infoData;
                        this.chooseTrSpec(record.specification_inner_sn);
                    },
                    dblclick: () => {
                        this.chooseTrSpec(record.specification_inner_sn);
                        this.openEditSpec();
                    }
                }
            };
        },
        chooseTrSpec(id) {
            // 选中行渲染
            this.specData.forEach((item) => {
                if (item.specification_inner_sn === id) {
                    item.isActive = 'active';
                    this.chooseTrSpecObj = item;
                    this.specClickFn(item);
                    this.$refs.specTable.setCurrentRow(item);
                } else {
                    item.isActive = '';
                }
            });
        },
        openCreateSpec() {
            this.isEdit = false;
            this.newSpecData.spec = '';
            this.newSpecData.pvd = '';
            this.newSpecData.unit = '';
            this.newSpecData.healthCareSn = '';
            this.newSpecData.sunSn = '';
            this.healthAndSunSnModal.data = [];
            if (this.chooseTrObj.product_inner_sn) {
                this.specTit = '新增规格型号';
                this.specSt = true;
                this.$Utils.globalDragCenterFn('unit_md');
            } else {
                this.$warning({
                    title: '操作提示',
                    content: '请先选中表格中的一项',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
            }
        },
        openEditSpec() {
            if (!this.chooseTrObj.product_inner_sn) {
                this.$warning({
                    title: '操作提示',
                    content: '请先选中表格中的一项',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return;
            }
            this.isEdit = true;
            this.$Utils.globalDragCenterFn('unit_md');
            if (this.chooseTrSpecObj.specification_inner_sn) {
                this.specTit = '修改规格型号';
                this.specSt = true;
                this.newSpecData.spec = this.chooseTrSpecObj.specification;
                this.newSpecData.pvd = this.chooseTrSpecObj.specification_vendor_sn;
                this.newSpecData.unit = this.chooseTrSpecObj.unit_name;
                this.newSpecData.healthCareSn = this.chooseTrSpecObj.default_health_care_sn;
                this.newSpecData.sunSn = this.chooseTrSpecObj.default_sun_sn;
                if (parseInt(this.chooseTrSpecObj.status) === 1) {
                    this.switchType = true;
                } else {
                    this.switchType = false;
                }
            } else {
                this.$warning({
                    title: '操作提示',
                    content: this.$Utils.tips().noSelected,
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
            }
        },
        async saveSpec() {
            let vid = this.chooseTrObj.vendor_inner_sn;
            let pid = this.chooseTrObj.product_inner_sn;
            let id = this.chooseTrSpecObj.specification_inner_sn;
            let status;
            if (this.switchType) {
                status = 1;
            } else {
                status = 0;
            }
            let params = {
                oid: vid,
                pid: pid,
                spec: '',
                pvd: '',
                status: status,
                default_health_care_sn: this.newSpecData.healthCareSn,
                default_sun_sn: this.newSpecData.sunSn
            };
            if (!this.isEdit) {
                let spec = this.newSpecData.spec;
                spec = this.$Utils.trim(spec);
                if (!spec || spec === '') {
                    this.$warning({
                        title: '操作提示',
                        content: '规格型号不能为空',
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    return;
                }
                let sameSt = true;
                this.specData.forEach((item) => {
                    if (item.specification === spec) {
                        sameSt = false;
                    }
                });
                if (!sameSt) {
                    this.$warning({
                        title: '操作提示',
                        content: '同一个产品的规格型号不可以重复',
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    return false;
                }
                params.spec = spec;
                params.pvd = this.$Utils.trim(this.newSpecData.pvd);
                await platformProductSpecAdd(params).then((res) => {
                    if (parseFloat(res.code) === 0) {
                        let chooseData = res.info;
                        chooseData.isActive = '';
                        chooseData.key = chooseData.specification_inner_sn;
                        // this.loadSpecFn(chooseData)
                        // this.specData.push(chooseData)

                        // 临时将新增数据插入表格中
                        const specTable = this.$refs.specTable,
                            defaultCurRow = specTable.getCurrentRecord();
                        specTable.insertAt(chooseData, defaultCurRow);
                        this.specData = specTable.getTableData().tableData;

                        this.chooseTrObj.tmpSpecList = this.specData;
                        this.specSt = false;
                        this.newSpecData.spec = '';
                        this.newSpecData.pvd = '';
                        this.newSpecData.healthCareSn = '';
                        this.newSpecData.sunSn = '';
                        this.specDelBtn = false;
                        this.chooseTrSpec(chooseData.specification_inner_sn);
                    } else {
                        this.$message.error(res.msg);
                    }
                });
            } else {
                let spec = this.newSpecData.spec;
                spec = this.$Utils.trim(spec);
                if (!spec || spec === '') {
                    this.$warning({
                        title: '操作提示',
                        content: '规格型号不能为空',
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    return;
                }

                params.spec = spec;
                params.psid = id;
                params.pvd = this.$Utils.trim(this.newSpecData.pvd);

                // 比较产品规格表单的 型号 编码 状态是否有修改操作 无修改不发请求
                let changed = false;
                const oldSpec = this.chooseTrSpecObj.specification;
                const oldPvd = this.chooseTrSpecObj.specification_vendor_sn;
                const oldStatus = this.chooseTrSpecObj.status;
                const oldHealtCareSn = this.chooseTrSpecObj.default_health_care_sn;
                const oldSunSn = this.chooseTrSpecObj.default_sun_sn;

                if (
                    params.spec !== oldSpec ||
                    params.pvd !== oldPvd ||
                    params.status !== oldStatus ||
                    params.default_health_care_sn !== oldHealtCareSn ||
                    params.default_sun_sn !== oldSunSn
                ) {
                    changed = true;
                }

                if (changed) {
                    await platformProductSpecEdit(params).then((res) => {
                        if (parseFloat(res.code) === 0) {
                            this.chooseTrObj.tmpSpecList = this.specData;
                            // this.loadSpecFn(this.chooseTrSpecObj)

                            // 临时更新表格数据
                            const specTable = this.$refs.specTable,
                                defaultCurRow = specTable.getCurrentRecord(),
                                defaultCurRowIndex = specTable.getRowIndex(defaultCurRow),
                                curEditData = {
                                    ...defaultCurRow,
                                    ...params,
                                    spec: params.spec,
                                    specification: params.spec,
                                    pvd: params.pvd,
                                    specification_vendor_sn: params.pvd,
                                    status: params.status,
                                    default_health_care_sn: params.healthCareSn,
                                    default_sun_sn: params.sunSn
                                };
                            this.specData.splice(defaultCurRowIndex, 1, curEditData);
                            this.chooseTrSpecObj = curEditData;
                            specTable.syncData();
                            this.chooseTrSpec(curEditData.specification_inner_sn);
                            this.specSt = false;
                        } else {
                            this.$message.error(res.msg);
                        }
                    });
                } else {
                    this.specSt = false;
                }
            }
        },
        // 删除
        deleteSpecBtn() {
            if (!this.chooseTrObj.product_inner_sn) {
                this.$warning({
                    title: '操作提示',
                    content: '请先选中表格中的一项',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return;
            }
            if (!this.chooseTrSpecObj.specification_inner_sn) {
                this.$warning({
                    title: '操作提示',
                    content: this.$Utils.tips().noSelected,
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return;
            }
            const _this = this;
            _this.$confirm({
                title: '删除',
                content: '确认要执行该操作吗？',
                centered: true,
                maskClosable: false,
                onOk() {
                    _this.delSpec();
                }
            });
        },
        async delSpec() {
            let vid = this.chooseTrObj.vendor_inner_sn;
            let pid = this.chooseTrObj.product_inner_sn;
            let id = this.chooseTrSpecObj.specification_inner_sn;

            await platformProductSpecDel(vid, pid, id).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.loadSpecFn();
                    this.specData = [];
                    this.chooseTrSpecObj = {};
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        openPackUnit() {
            if (!this.chooseTrSpecObj.specification_inner_sn) {
                this.$warning({
                    title: '操作提示',
                    content: this.$Utils.tips().noSelected,
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return;
            }
            this.packUnitName = this.leftTitle + this.chooseTrObj.name + ' - ' + this.chooseTrSpecObj.specification;
            this.packUnitSt = true;
            let unitPg = this.$refs.pageUnitPg;
            if (unitPg) {
                unitPg.unitPageInit();
                this.$Utils.globalDragCenterFn('pack_unit');
            } else {
                this.$Utils.globalDragCenterFn('pack_unit', true);
            }
        },
        handlePackUnitSaveDoneFn(commonUseObj) {
            if (commonUseObj) {
                const curRow = this.$refs.specTable.getCurrentRecord(),
                    curRowIndex = this.$refs.specTable.getRowIndex(curRow);
                this.$set(this.specData[curRowIndex], 'common_use_unit', commonUseObj.name);
                this.$set(this.specData[curRowIndex], 'common_use_unit_inner_sn', commonUseObj.key);
            }
        },
        changeSwitch(status) {
            this.switchType = status.target.checked;
        },
        autoTabHeight() {
            // 自动计算表格高度
            let orderHeight = document.body.clientHeight - 48 - 56 - 256 - 6;
            this.tableHeight = orderHeight;
            this.tabScrollHeight = this.tableHeight - 100;
        },
        handlePrint() {
            const otherCertImgs = this.otherCertImgs;
            if (!this.gicMdrfObj.images.length && !otherCertImgs.length) {
                this.$warning({
                    title: '操作提示',
                    content: '暂无注册证可打印',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return;
            }
            const printContent = JSON.stringify({
                printData: this.gicMdrfObj.images.concat(otherCertImgs).map((i) => this.$Utils.getPictureSrc(i.url)),
                printType: 11 // printType  11、多张图片
            });
            window.localStorage.printContent = printContent;
            const routeUrl = this.$router.resolve({
                path: 'print',
                name: 'print'
            });
            window.open(routeUrl.href, '_blank');
        },
        getOtherCertImgsForPrint(imgs) {
            this.otherCertImgs = imgs;
        },
        // 获取医保编号输入
        healthCareSnChangeFn(e) {
            if (typeof e.sn === 'undefined') {
                // 来自用户输入
                const { value } = e.target;
                this.newSpecData.healthCareSn = value;
            } else {
                // 来自下拉选择
                this.newSpecData.healthCareSn = e.sn;
            }
        },
        // 获取阳光流水号输入
        sunSnChangeFn(e) {
            if (typeof e.sn === 'undefined') {
                // 来自用户输入
                const { value } = e.target;
                this.newSpecData.sunSn = value;
            } else {
                // 来自下拉选择
                this.newSpecData.sunSn = e.sn;
            }
        },
        // 获取医保编号/阳光流水号下拉
        async getProductSnAPIFn(isHealthCare) {
            if (!this.isEdit) {
                return;
            }
            this.healthAndSunSnModal.data = [];
            this.newSpecData.default_healthCareSn = '';
            this.newSpecData.default_sunSn = '';

            let params = {
                vendor_inner_sn: this.chooseTrObj.vendor_inner_sn,
                product_inner_sn: this.chooseTrObj.product_inner_sn,
                specification_inner_sn: this.chooseTrSpecObj.specification_inner_sn,
                // pageNum: 1,
                // pageSize: 10,
                type: isHealthCare ? 'HEALTH_CARE_SN' : 'SUN_SN'
            };
            await platforProductSnListAPI({ params }).then((res) => {
                this.healthAndSunSnModal.data = res.list.map((item, index) => {
                    if (item.is_default) {
                        if (isHealthCare) {
                            this.newSpecData.default_healthCareSn = item.sn;
                        } else {
                            this.newSpecData.default_sunSn = item.sn;
                        }
                    }
                    return {
                        sn: item.sn,
                        sn_inner_sn: item.sn_inner_sn,
                        is_default: item.is_default,
                        index: index,
                        type: item.type,
                        vendor_inner_sn: item.vendor_inner_sn,
                        product_inner_sn: item.product_inner_sn,
                        specification_inner_sn: item.specification_inner_sn
                    };
                });
            });
        },
        // 产品新增模态框-取消按钮
        onProductAddCancel() {
            this.specSt = false;
            this.newSpecData.spec = '';
            this.newSpecData.pvd = '';
            this.newSpecData.unit = '';
            this.newSpecData.healthCareSn = '';
            this.newSpecData.sunSn = '';
        },
        // 【医保编号/阳光流水号】新增模态框-取消按钮
        onHealthAndSunAddCancel() {
            this.healthAndSunSnModal.visible = false;
            if (this.isHealthCareModal) {
                this.newSpecData.healthCareSn = this.newSpecData.default_healthCareSn;
            } else {
                this.newSpecData.sunSn = this.newSpecData.default_sunSn;
            }
        },
        // 【医保编号/阳光流水号】模态框
        onHealthAndSunSnMore(isHealthCare) {
            if (!this.isEdit) {
                let spec = this.newSpecData.spec;
                spec = this.$Utils.trim(spec);
                if (!spec || spec === '') {
                    this.$warning({
                        title: '操作提示',
                        content: '规格型号不能为空',
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    return;
                }
            }
            if (isHealthCare) {
                this.isHealthCareModal = true;
                this.getProductSnAPIFn(true);
                this.healthAndSunSnModal.title = '医保编号';
                this.healthAndSunSnModal.visible = true;
                this.$Utils.globalDragCenterFn('health_and_sun_md');
            } else {
                this.isHealthCareModal = false;
                this.getProductSnAPIFn(false);
                this.healthAndSunSnModal.title = '阳光流水号';
                this.healthAndSunSnModal.visible = true;
                this.$Utils.globalDragCenterFn('health_and_sun_md');
            }
        },
        // 【医保编号/阳光流水号】模态框按钮组
        groupBtnFn(condition) {
            // 初始化数据
            this.healthAndSunAddModal.input_sn = '';
            this.healthAndSunAddModal.is_default = false;

            switch (condition) {
                case 'add':
                    this.healthAndSunAddModal.title = this.isHealthCareModal ? '医保编号-新增' : '阳光流水号-新增';
                    this.healthAndSunAddModal.visible = true;
                    this.healthAndSunAddModal.isEdit = false;
                    this.$Utils.globalDragCenterFn('health_and_sun_add_md');
                    break;
                case 'edit':
                    this.healthAndSunAddModal.title = this.isHealthCareModal ? '医保编号-修改' : '阳光流水号-修改';
                    this.healthAndSunAddModal.visible = true;
                    this.healthAndSunAddModal.isEdit = true;
                    this.healthAndSunAddModal.input_sn = this.healthAndSunAddModal.currentObj.sn;
                    this.healthAndSunAddModal.is_default = this.healthAndSunAddModal.currentObj.is_default;
                    this.$Utils.globalDragCenterFn('health_and_sun_add_md');
                    break;
                case 'delete': {
                    const _this = this;
                    _this.$confirm({
                        title: '删除',
                        content: '确认要执行该操作吗？',
                        centered: true,
                        maskClosable: false,
                        onOk() {
                            _this.onHealthAndSunDel();
                        }
                    });
                    break;
                }
                default:
                    break;
            }
        },
        // 【医保编号/阳光流水号】新增/编辑
        async onHealthAndSunSave() {
            let params = {
                // 新增时，使用
                is_default: this.healthAndSunAddModal.is_default,
                sn: this.healthAndSunAddModal.input_sn,
                product_inner_sn: this.chooseTrObj.product_inner_sn,
                vendor_inner_sn: this.chooseTrObj.vendor_inner_sn,
                specification_inner_sn: this.isEdit
                    ? this.chooseTrSpecObj.specification_inner_sn
                    : this.newSpecData.spec,
                type: this.isHealthCareModal ? 'HEALTH_CARE_SN' : 'SUN_SN'
            };

            if (this.healthAndSunAddModal.isEdit) {
                params.sn_inner_sn = this.healthAndSunAddModal.currentObj.sn_inner_sn;
                params.product_inner_sn = this.healthAndSunAddModal.currentObj.product_inner_sn;
                params.vendor_inner_sn = this.healthAndSunAddModal.currentObj.vendor_inner_sn;
                params.specification_inner_sn = this.healthAndSunAddModal.currentObj.specification_inner_sn;
                params.type = this.healthAndSunAddModal.currentObj.type;

                await productSnEditAPI(params).then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.healthAndSunAddModal.visible = false;
                        this.healthAndSunAddModal.objSelected = false;
                        if (this.isHealthCareModal) {
                            this.getProductSnAPIFn(true);
                            if (this.newSpecData.default_healthCareSn === '') {
                                this.newSpecData.healthCareSn = '';
                            }
                            this.newSpecData.healthCareSn = this.newSpecData.default_healthCareSn;
                        } else {
                            this.getProductSnAPIFn(false);
                            if (this.newSpecData.default_sunSn === '') {
                                this.newSpecData.sunSn = '';
                            }
                            this.newSpecData.sunSn = this.newSpecData.default_sunSn;
                        }
                    } else {
                        this.$message.error(res.msg);
                    }
                });
            } else {
                await productSnAddAPI(params).then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.healthAndSunAddModal.visible = false;
                        this.healthAndSunAddModal.objSelected = false;
                        if (this.isHealthCareModal) {
                            this.getProductSnAPIFn(true);
                            this.newSpecData.healthCareSn = this.newSpecData.default_healthCareSn;
                        } else {
                            this.getProductSnAPIFn(false);
                            this.newSpecData.sunSn = this.newSpecData.default_sunSn;
                        }
                    } else {
                        this.$message.error(res.msg);
                    }
                });
            }
        },
        // 【医保编号/阳光流水号】删除
        async onHealthAndSunDel() {
            let params = {
                is_default: this.healthAndSunAddModal.currentObj.is_default,
                sn: this.healthAndSunAddModal.currentObj.sn,
                sn_inner_sn: this.healthAndSunAddModal.currentObj.sn_inner_sn,
                product_inner_sn: this.healthAndSunAddModal.currentObj.product_inner_sn,
                vendor_inner_sn: this.healthAndSunAddModal.currentObj.vendor_inner_sn,
                specification_inner_sn: this.healthAndSunAddModal.currentObj.specification_inner_sn,
                type: this.healthAndSunAddModal.currentObj.type
            };
            await productSnDelAPI({ data: params }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.healthAndSunAddModal.visible = false;
                    this.loadSpecFn(this.chooseTrSpecObj);
                    if (this.isHealthCareModal) {
                        this.getProductSnAPIFn(true);
                        if (params.is_default) {
                            this.newSpecData.default_healthCareSn = '';
                            this.newSpecData.healthCareSn = '';
                        }
                    } else {
                        this.getProductSnAPIFn(false);
                        if (params.is_default) {
                            this.newSpecData.default_sunSn = '';
                            this.newSpecData.sunSn = '';
                        }
                    }
                    this.healthAndSunAddModal.objSelected = false;
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 【医保编号/阳光流水号】模态框表格单击事件
        Table_ClickFn(e) {
            this.healthAndSunAddModal.currentObj = e.row;
            this.healthAndSunAddModal.objSelected = true;
        },
        // 【医保编号/阳光流水号】模态框表格双击事件
        Table_DblClickFn() {
            this.healthAndSunAddModal.title = this.isHealthCareModal ? '医保编号-修改' : '阳光流水号-修改';
            this.healthAndSunAddModal.visible = true;
            this.healthAndSunAddModal.isEdit = true;
            if (this.isHealthCareModal) {
                this.healthAndSunAddModal.input_sn = this.healthAndSunAddModal.currentObj.sn;
                this.healthAndSunAddModal.is_default = this.healthAndSunAddModal.currentObj.is_default;
            } else {
                this.healthAndSunAddModal.input_sn = this.healthAndSunAddModal.currentObj.sn;
                this.healthAndSunAddModal.is_default = this.healthAndSunAddModal.currentObj.is_default;
            }
            this.$Utils.globalDragCenterFn('health_and_sun_add_md');
        },
        /**
         * 同步按钮对应方法
         */
        handleSync() {
            if (!this.isChooseTr) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择产品',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
            } else {
                const obj = this.chooseTrObj;
                this.sync.visible = true;
                this.sync.isSync = true;
                this.platformProductSyncAPIFn(obj.vendor_inner_sn, obj.product_inner_sn);
            }
        },
        onSyncConfirm() {
            this.sync.visible = false;
        },
        /**
         * 同步平台产品信息与所有仓库的库存产品信息接口
         * @param vid 厂商id
         * @param pid 产品id
         */
        async platformProductSyncAPIFn(vid, pid) {
            await platformProductSyncAPI(vid, pid).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.sync.isSync = false;
                } else {
                    this.$message.error(res.msg);
                    this.sync.isSync = false;
                }
            });
        }
    },
    watch: {
        highSearch(val) {
            let box = document.getElementsByClassName('box');
            if (val) {
                this.boxTopHeight -= 38;
                for (let i = 0; i < box.length; i++) {
                    document.getElementsByClassName('box')[i].style.top = '98px';
                    document.getElementsByClassName('boxtop')[i].style.height = this.boxTopHeight + 55 + 'px';
                }
            } else {
                this.boxTopHeight += 38;
                for (let i = 0; i < box.length; i++) {
                    document.getElementsByClassName('box')[i].style.top = '60px';
                    document.getElementsByClassName('boxtop')[i].style.height = this.boxTopHeight + 55 + 'px';
                }
            }
        }
    },
    mounted() {
        // 平台 -> 0
        // 厂商 -> 3
        this.nowSystemType = parseInt(this.$cookie.get('userSystemType'));
        this.nowOid = parseInt(this.$cookie.get('userbelong'));
        if (this.nowSystemType === 3) {
            this.columns.splice(1, 1);
            this.columns[0].width = '40%';
            this.columns[1].width = '25%';
            this.columns[2].width = '25%';
        }
        this.getUploadTokenAPIFn();
        this.loadTable();
        this.getCategoryListFn();
        this.get68ParentListFn();
        this.autoTabHeight();
        this.boxTopHeight = this.scrollYNumber - 56;
        this.boxDownHeight = this.scrollYNumber - 12;
        this.dragControllerDiv();
        this.dragControllerDivHoriz();
    }
};
</script>

<style scoped lang="less">
@import '../../../../public/style/ptmStyle1';

.upload-count {
    .em {
        font-size: 22px;
        vertical-align: middle;
        margin-right: 3px;
        float: left;
        height: 20px;
        line-height: 27px;
    }
}
</style>
<style scoped>
.box {
    width: 100%;
    top: 60px;
    bottom: 0px;
    overflow: hidden;
    position: absolute;
    background: #ffffff;
}
.boxtop {
    /*width:calc(30% - 10px);*/
    width: 100%;
    padding: 8px;
    height: calc(50% - 3px);
    float: left;
}
.resize {
    width: 100%;
    height: 5px;
    cursor: w-resize;
    cursor: s-resize;
    float: left;
    border-bottom: 1px solid #e8e8e8;
    border-top: 1px solid #e8e8e8;
}
.resizeHoriz {
    width: 5px;
    height: 100%;
    margin-left: 8px;
    cursor: n-resize;
    cursor: e-resize;
    float: left;
    border-left: 1px solid #e8e8e8;
    border-right: 1px solid #e8e8e8;
}
.boxdown {
    float: left;
    width: 100%;
    height: 50%;
}
</style>
