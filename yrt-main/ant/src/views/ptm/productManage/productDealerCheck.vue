<!--suppress ALL -->
<template>
    <div class="product-page">
        <div class="todo-part">
            <a-row>
                <a-col :span="12" class="btn-count">
                    <a-button class="btn-nom mgr-10" @click="openCreate" type="primary">新增</a-button>
                    <a-button
                        v-if="nowSystemType != 3"
                        :disabled="isSend"
                        class="btn-nom mgr-10"
                        type="primary"
                        @click="openEdit"
                        >修改</a-button
                    >
                    <a-button
                        v-if="nowSystemType != 3"
                        :disabled="isSend"
                        class="btn-nom mgr-10"
                        type="primary"
                        @click="sendCheck"
                        >送审</a-button
                    >
                    <a-button
                        v-if="nowSystemType != 3"
                        :disabled="btnDisabled"
                        class="btn-nom"
                        @click="deleteBtn"
                        type="danger"
                        >删除</a-button
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
                            @click="initPage"
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
                            <span class="label">产品分类</span>
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
                <!--                <div v-bind:style="{height : tableHeight + 'px'}" class="table-part">-->
                <!--                :height="tabScrollHeight + 55"-->
                <div class="table-part" style="margin-top: 0">
                    <!--<a-table-->
                    <!--:loading="pageInit.loading"-->
                    <!--:columns="columns"-->
                    <!--:dataSource="tableData"-->
                    <!--:scroll = "{y : tabScrollHeight}"-->
                    <!--:pagination="{-->
                    <!--size: 'small',-->
                    <!--total: pageInit.total,-->
                    <!--pageSize: pageInit.pageSize,-->
                    <!--showSizeChanger: true,-->
                    <!--showQuickJumper: true-->
                    <!--}"-->
                    <!--:customRow="customRow"-->
                    <!--@change="changePaginationFn"-->
                    <!--&gt;-->
                    <!--<span slot="tags" slot-scope="tags">-->
                    <!--<div class="blue">{{tags}}</div>-->
                    <!--</span>-->
                    <!--</a-table>-->
                    <vxe-grid
                        ref="xTable"
                        size="small"
                        :columns="columns"
                        :height="boxTopHeight"
                        resizable
                        auto-resize
                        highlight-current-row
                        @cell-click="cellClickFn"
                        @cell-dblclick="cellDblclickFn"
                        border
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
                    style="width: calc(100% - 4px)"
                >
                    <div class="spec-box table-part" style="height: calc(100% - 11px); margin-left: 8px">
                        <div class="todo_box" style="border-bottom: none">
                            <a-button class="btn-nom mgr-10" :disabled="isSend" @click="openCreateSpec" type="primary"
                                >新增</a-button
                            >
                            <a-button class="btn-nom cut mgr-10" :disabled="isSend" @click="openEditSpec" type="primary"
                                >修改</a-button
                            >
                            <a-button class="btn-nom mgr-10" :disabled="specDelBtn" @click="deleteSpecBtn" type="danger"
                                >删除</a-button
                            >
                            <!--<a-button  class="btn-nom mgr-10" @click="openPackUnit" type="primary">包装单位</a-button>-->
                        </div>
                        <div style="width: 100%; margin-top: 54px; padding: 0 10px">
                            <!-- 此表格调用platformProductSpecList获取数据 -->
                            <vxe-grid
                                v-if="nowSystemType === 1"
                                ref="specTable"
                                class="specTable"
                                size="small"
                                :columns="spec_columns_Hospital"
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
                                v-if="nowSystemType === 2"
                                ref="specTable"
                                class="specTable"
                                size="small"
                                :columns="spec_columns_Dealer"
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
                        </div>
                    </div>
                    <div class="resizeHoriz"></div>
                    <div class="tab-table-wrap minimal more2" style="height: calc(100% - 3px); margin-left: 8px">
                        <div
                            style="
                                position: absolute;
                                width: 100%;
                                height: calc(100% - 50px);
                                margin-top: 41px;
                                border-left: 1px solid #e4e4e4;
                                border-right: 1px solid #e4e4e4;
                            "
                        ></div>
                        <!-- tab切换区域开始 -->
                        <bottom-tabs-wrapper>
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
                                                        <a-button
                                                            :disabled="isSend"
                                                            class="btn-nom mgr-10"
                                                            type="primary"
                                                            >上传图片</a-button
                                                        >
                                                    </div>
                                                </a-upload>
                                            </div>
                                            <div
                                                class="
                                                    bottom-tabs-content-right bottom-pictures-wrapper
                                                    scroll-vertical-wrapper
                                                "
                                            >
                                                <div class="scroll-vertical-content">
                                                    <picture-box
                                                        v-for="(item, index) of goodsImgList"
                                                        :key="index"
                                                        :resourceKey="item.url"
                                                        :deleteAble="item.status == 2 || item.status == 4"
                                                        @preview="previewImg($Utils.getPictureSrc(item.url))"
                                                        @delete="deleteImg(item)"
                                                    >
                                                        <span class="img_sp" style="z-index: 999">
                                                            <span v-if="item.status == 2">草稿</span>
                                                            <span class="yellow" v-if="item.status == 3">待审核</span>
                                                            <a-tooltip v-if="item.status == 4">
                                                                <template slot="title"> 点击查看原因 </template>
                                                                <span
                                                                    class="red"
                                                                    @click="openAudit(item.audit_description)"
                                                                    >不通过</span
                                                                >
                                                            </a-tooltip>
                                                        </span>
                                                    </picture-box>
                                                    <div class="no-data-box" v-if="goodsImgList.length <= 0">
                                                        <div><a-icon type="file-excel" /></div>
                                                        暂无图片
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <!-- 注册证登记表 -->
                                        <div class="bottom-tabs-container" v-if="item.key == 'regForm'">
                                            <div class="bottom-tabs-content-left bottom-buttons-vertical-wrapper">
                                                <a-button
                                                    :disabled="isSend"
                                                    @click="openGicMdrf"
                                                    class="btn-nom mgr-10"
                                                    type="primary"
                                                >
                                                    新增
                                                </a-button>
                                                <a-button
                                                    :disabled="isSend"
                                                    @click="editGicMdrf"
                                                    class="btn-nom mgr-10"
                                                    type="primary"
                                                >
                                                    修改
                                                </a-button>
                                                <a-button
                                                    @click="delGicMdrf"
                                                    :disabled="mdrfDelBtn"
                                                    class="btn-nom mgr-10"
                                                    type="danger"
                                                >
                                                    删除
                                                </a-button>
                                            </div>
                                            <div class="bottom-tabs-content-right bottom-table-wrapper">
                                                <vxe-grid
                                                    ref="mdrfTable"
                                                    size="small"
                                                    :columns="mdrf_columns"
                                                    :height="boxDownHeight - 72"
                                                    border
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
            cancelText="关闭"
            okText="打印"
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
            <template slot="footer">
                <a-button key="back" @click="createSt = false">取消</a-button>
                <a-button type="primary" @click="saveGoodsFn(2)"> 保存 </a-button>
            </template>
            <create-cp ref="createPage" @getData="getCpmData"></create-cp>
        </a-modal>
        <a-modal
            title="修改"
            :visible="editSt"
            :width="900"
            okText="保存"
            :maskClosable="false"
            :centered="true"
            @cancel="closeEditGoods"
            class="modal-form-input-scoped global-drag edit"
        >
            <div v-globalDrag="{ el: 'edit' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ leftTitle }} - 修改
                </h6>
            </div>
            <template slot="footer">
                <a-button key="back" @click="closeEditGoods">
                    <span v-if="!isSend">取消</span>
                    <span v-else>关闭</span>
                </a-button>
                <a-button v-if="!isSend" type="primary" @click="editGoodsFn"> 保存 </a-button>
            </template>
            <edit-cp ref="editPage" :modalData="editTrObj" @getData="getEditData" @getEditInfo="sendEditInfo"></edit-cp>
            <!--:modalData="editTrObj"-->
        </a-modal>
        <!-- title="录入"
        :visible = "entrySt"
        :width= 1150
        okText="保存"
        @ok="entryGicMdrf"
        :maskClosable="false"
        :centered="true"
        @cancel = "entrySt = false"
        class="modal-form-input-scoped global-drag entryModal" -->
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
                <a-button key="back" @click="entrySt = false">
                    <span v-if="!isSend">取消</span>
                    <span v-else>关闭</span>
                </a-button>
                <a-button @click="handlePrint">打印</a-button>
                <a-button v-if="!isSend" type="primary" @click="entryGicMdrf"> 保存 </a-button>
            </template>
        </a-modal>
        <a-modal
            :title="specTit"
            :visible="specSt"
            :maskClosable="false"
            :centered="true"
            :width="540"
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
                <li v-if="chooseTrObj.operation == 1" style="padding-top: 24px">
                    <span class="label" style="margin-right: 10px">包装单位</span>
                    <a-input placeholder="" v-model="newSpecData.unit" style="width: 80%" class="input" />
                </li>
                <li style="padding-top: 24px" v-if="nowSystemType === 2">
                    <span class="label" style="margin-right: 10px; vertical-align: middle">医保编号</span>
                    <!-- <a-select
                        show-arrow
                        placeholder=""
                        :value="newSpecData.healthCareSn"
                        :not-found-content="null"
                        option-filter-prop="children"
                        style="width: 73%; display: inline-block"
                        :filter-option="filterOption"
                        @focus="getProductSnAPIFn(true)"
                        @change="handleChange"
                    >
                        <a-select-option  
                            v-for="item in healthAndSunSnModal.data"
                            :key="item.index"
                            :value="item.sn">
                            {{item.sn}}
                        </a-select-option>
                    </a-select> -->
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
                <li style="padding-top: 24px" v-if="nowSystemType === 2">
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
            </div>
            <template slot="footer">
                <a-button key="back" @click="onProductAddCancel">
                    <span v-if="!isSend">取消</span>
                    <span v-else>关闭</span>
                </a-button>
                <a-button v-if="!isSend" type="primary" @click="saveSpec"> 保存 </a-button>
            </template>
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
            <pack-unit ref="pageUnitPg" :packUnitObj="packUnitObj"></pack-unit>
        </a-modal>
        <a-modal
            :visible="chooseOrgMd.alert"
            :maskClosable="false"
            :width="600"
            :centered="true"
            :footer="null"
            @cancel="chooseOrgMd.alert = false"
            :zIndex="3"
            class="scoped modal-form-input-scoped global-drag modal-choose-org"
        >
            <div v-globalDrag="{ el: 'modal-choose-org' }" class="modal-header-wrap">
                <h6>
                    <img src="/images/logo/logo-left.svg" alt="" class="modal-logo-left" />
                    {{ chooseOrgMd.title }}
                </h6>
            </div>
            <div style="height: 48px"></div>
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
    </div>
</template>

<script>
import BottomTabsWrapper from '@/components/BaseBottom/BottomTabsWrapper';
import BottomPictures from '@/components/BaseBottom/BottomPictures';
import PictureBox from '@/components/Picture/PictureBox';
import createCp from './module/createCheckGoods';
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
    tmpAddAPI,
    tmpListAPI,
    tmpDelAPI,
    tmpEditAPI,
    tmpComposeEditAPI,
    tmpImageAddAPI,
    tmpImageListAPI,
    tmpMdrfListAPI,
    tmpMdrfAddAPI,
    tmpMdrfDelAPI,
    tmpMdrfEditAPI,
    tmpMdrfInfoAPI,
    tmpSpecListAPI,
    tmpSpecAddAPI,
    tmpSpecEditAPI,
    tmpSpecDelAPI,
    tmpImageDelAPI,
    tmpSendAuditAPI,
    platformOrgImgListAPI,
    platforProductSnListAPI,
    productSnAddAPI,
    productSnEditAPI,
    productSnDelAPI
} from '@/service/pageAjax';

export default {
    name: 'productReleased',
    components: {
        PictureBox,
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
                source: parseFloat(this.$cookie.get('userSystemType')),
                name: '',
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
            specLoading: false,
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
                    minWidth: '400',
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
                    minWidth: '300',
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
                    minWidth: '150',
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
                    minWidth: '150',
                    slots: {
                        default: ({ row }) => {
                            return [row.text_68];
                        }
                    }
                },
                {
                    title: '来自',
                    dataIndex: 'sourceName',
                    align: 'center',
                    minWidth: '50',
                    slots: {
                        default: ({ row }) => {
                            return [row.sourceName];
                        }
                    }
                },
                {
                    title: '状态',
                    dataIndex: 'status',
                    align: 'center',
                    minWidth: '30',
                    slots: {
                        default: ({ row }) => {
                            let val;
                            let auditSt;
                            let des;
                            let text = row.status;
                            if (parseInt(text) === 1) {
                                val = '有效';
                                auditSt = 'color: #16a935';
                            }
                            if (parseInt(text) === 0) {
                                auditSt = 'color: red';
                                val = '无效';
                            }
                            if (parseInt(text) === 2) {
                                auditSt = 'color: #999';
                                val = '草稿';
                            }
                            if (parseInt(text) === 3) {
                                auditSt = 'color: #eb981c';
                                val = '待审核';
                            }
                            if (parseInt(text) === 4) {
                                auditSt = 'color: red';
                                val = '不通过';
                                des = '点击查看原因';
                            }
                            let openDes = () => {
                                if (parseInt(text) === 4) {
                                    this.$warning({
                                        title: '不通过原因',
                                        content: row.audit_description,
                                        okText: '知道了',
                                        centered: true,
                                        maskClosable: true
                                    });
                                }
                            };
                            return [
                                <a-tooltip>
                                    <span style={auditSt} onClick={openDes}>
                                        {val}
                                    </span>
                                    <template placement="topLeft" slot="title">
                                        {des}
                                    </template>
                                </a-tooltip>
                            ];
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
                    name: '营业执照',
                    key: 'bizLicense'
                },
                {
                    name: '医疗器械生产许可证',
                    key: 'prodLicense'
                }
                // {
                //     name: '质量检测报告',
                //     key: 'test'
                // },
                // {
                //     name: '其它',
                //     key: 'other'
                // }
            ],
            sourceName: ['平台', '医院', '经销商', '厂商'],
            previewVisible: false,
            previewImage: '',
            createDataObj: [],
            newEditDataObj: [],
            goodsImgList: [],
            certImgList: [], // TODO: 疑似无用变量
            certObj: {
                begin_date: '',
                end_date: ''
            },
            mdrfImgList: [], // TODO: 疑似无用变量
            mdrf_columns: [
                {
                    title: '注册号',
                    dataIndex: 'register_sn',
                    filed: 'register_sn',
                    align: 'center',
                    minWidth: '150',
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
                    showOverflow: true,
                    showHeaderOverflow: true,
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
                    showOverflow: true,
                    showHeaderOverflow: true,
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
                    minWidth: '150',
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
                    showOverflow: true,
                    showHeaderOverflow: true,
                    slots: {
                        default: ({ row }) => {
                            return [row.issue_date];
                        }
                    }
                },
                {
                    title: '状态',
                    dataIndex: 'status',
                    align: 'center',
                    filed: 'status',
                    minWidth: '100',
                    showOverflow: true,
                    showHeaderOverflow: true,
                    slots: {
                        default: ({ row }) => {
                            let val;
                            let auditSt;
                            let des;
                            let text = row.status;
                            if (parseInt(text) === 1) {
                                val = '有效';
                                auditSt = 'color: #16a935';
                            }
                            if (parseInt(text) === 0) {
                                auditSt = 'color: red';
                                val = '无效';
                            }
                            if (parseInt(text) === 2) {
                                auditSt = 'color: #999';
                                val = '草稿';
                            }
                            if (parseInt(text) === 3) {
                                auditSt = 'color: #eb981c';
                                val = '待审核';
                            }
                            if (parseInt(text) === 4) {
                                auditSt = 'color: red';
                                val = '不通过';
                                des = '点击查看原因';
                            }
                            let openDes = () => {
                                if (parseInt(text) === 4) {
                                    this.$warning({
                                        title: '不通过原因',
                                        content: row.audit_description,
                                        okText: '知道了',
                                        centered: true,
                                        maskClosable: true
                                    });
                                }
                            };
                            return [
                                <a-tooltip>
                                    <span style={auditSt} onClick={openDes}>
                                        {val}
                                    </span>
                                    <template placement="topLeft" slot="title">
                                        {des}
                                    </template>
                                </a-tooltip>
                            ];
                        }
                    }
                }
            ],
            mdrfData: [],
            mdrfLoading: false,
            chooseTrMdrfObj: {},
            examImgList: [],
            bizLicenseImgList: [],
            prodLicenseImgList: [],
            otherImgList: [],
            userSystemType: parseFloat(this.$cookie.get('userSystemType')),
            // 医院端 - 下方列表
            spec_columns_Hospital: [
                {
                    title: '产品编码',
                    dataIndex: 'specification_vendor_sn',
                    align: 'center',
                    minWidth: '200',
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
                    minWidth: '200',
                    slots: {
                        default: ({ row }) => {
                            return [row.specification];
                        }
                    }
                },
                {
                    title: '状态',
                    dataIndex: 'status',
                    align: 'center',
                    minWidth: '100',
                    slots: {
                        default: ({ row }) => {
                            let val;
                            let auditSt;
                            let des;
                            let text = row.status;
                            if (parseInt(text) === 1) {
                                val = '有效';
                                auditSt = 'color: #16a935';
                            }
                            if (parseInt(text) === 0) {
                                auditSt = 'color: red';
                                val = '无效';
                            }
                            if (parseInt(text) === 2) {
                                auditSt = 'color: #999';
                                val = '草稿';
                            }
                            if (parseInt(text) === 3) {
                                auditSt = 'color: #eb981c';
                                val = '待审核';
                            }
                            if (parseInt(text) === 4) {
                                auditSt = 'color: red';
                                val = '不通过';
                                des = '点击查看原因';
                            }
                            let openDes = () => {
                                if (parseInt(text) === 4) {
                                    this.$warning({
                                        title: '不通过原因',
                                        content: row.audit_description,
                                        okText: '知道了',
                                        centered: true,
                                        maskClosable: true
                                    });
                                }
                            };
                            return [
                                <a-tooltip>
                                    <span style={auditSt} onClick={openDes}>
                                        {val}
                                    </span>
                                    <template placement="topLeft" slot="title">
                                        {des}
                                    </template>
                                </a-tooltip>
                            ];
                        }
                    }
                }
            ],
            // 经销商端-下方列表
            spec_columns_Dealer: [
                {
                    title: '产品编码',
                    dataIndex: 'specification_vendor_sn',
                    align: 'center',
                    minWidth: '200',
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
                    minWidth: '200',
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
                    title: '状态',
                    dataIndex: 'status',
                    align: 'center',
                    minWidth: '100',
                    slots: {
                        default: ({ row }) => {
                            let val;
                            let auditSt;
                            let des;
                            let text = row.status;
                            if (parseInt(text) === 1) {
                                val = '有效';
                                auditSt = 'color: #16a935';
                            }
                            if (parseInt(text) === 0) {
                                auditSt = 'color: red';
                                val = '无效';
                            }
                            if (parseInt(text) === 2) {
                                auditSt = 'color: #999';
                                val = '草稿';
                            }
                            if (parseInt(text) === 3) {
                                auditSt = 'color: #eb981c';
                                val = '待审核';
                            }
                            if (parseInt(text) === 4) {
                                auditSt = 'color: red';
                                val = '不通过';
                                des = '点击查看原因';
                            }
                            let openDes = () => {
                                if (parseInt(text) === 4) {
                                    this.$warning({
                                        title: '不通过原因',
                                        content: row.audit_description,
                                        okText: '知道了',
                                        centered: true,
                                        maskClosable: true
                                    });
                                }
                            };
                            return [
                                <a-tooltip>
                                    <span style={auditSt} onClick={openDes}>
                                        {val}
                                    </span>
                                    <template placement="topLeft" slot="title">
                                        {des}
                                    </template>
                                </a-tooltip>
                            ];
                        }
                    }
                }
            ],
            // 产品规格型号
            specData: [],
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
            isEdit: false, // [规格型号]是否是编辑
            packUnitObj: {},
            switchType: true,
            btnDisabled: true,
            isSend: false,
            specDelBtn: true,
            mdrfDelBtn: true,
            otherCertImgs: [] // 针对于打印的，“营业执照”和“医疗器械生产许可证”的图片数组，从entry-gic组件中回传回来
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
                        if (moveLen < 380) moveLen = 380; // 左边区域的最小宽度为200px
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
            this.$Utils.globalDragCenterFn('modal-choose-org');
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
        cellClickFn(row) {
            let obj = row.row || row;
            // const timer = clickDebounce.getTimer()
            // if (timer) { // 清除单击事件 防止和双击事件同时触发
            //     clickDebounce.clearTimer()
            // }
            this.chooseTrEvent(obj);
        },
        // 双击行
        cellDblclickFn({ row }) {
            const timer = clickDebounce.getTimer();
            if (timer) {
                // 清除单击事件 防止和双击事件同时触发
                clickDebounce.clearTimer();
            }
            this.chooseTrEvent(row, true);
            this.openEdit();
        },
        chooseTrEvent(record) {
            // 选中事件
            this.isChooseTr = true;
            this.chooseTrObj = record;
            if (record.status === 2) {
                this.btnDisabled = false;
            } else {
                this.btnDisabled = true;
            }
            if (record.status === 3) {
                this.isSend = true;
            } else {
                this.isSend = false;
            }
            this.chooseTrSpecObj = {};
            this.chooseTrMdrfObj = {};
            this.mdrfDelBtn = true;
            this.specDelBtn = true;
            if (record.status === 2 || record.status === 4) {
                this.btnDisabled = false;
            }
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
            this.getGoodsImg();
            this.getBizLicenseImg();
            this.getProdLicenseImg();
            this.getGicMdrf();
            this.specData = this.chooseTrObj.tmpSpecList.map((item) => {
                item.isActive = '';
                item.key = item.specification_inner_sn;
                if (item.status === 3) {
                    this.isSend = true;
                }
                return item;
            });
            this.$nextTick(() => {
                const xTable = this.$refs.xTable;
                xTable.setCurrentRow(this.chooseTrObj); // 默认选中
            });
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
            this.createDataObj = JSON.parse(JSON.stringify(data));
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
        async saveGoodsFn(type) {
            // 保存
            this.$refs.createPage.returnPageData();
            if (this.createDataObj.specAndMinUnitList.length === 0) {
                return false;
            }
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

            if (dataObj.vendor_inner_sn === '' || !dataObj.vendor_inner_sn) {
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
            dataObj.status = type;
            dataObj.org_id = this.$cookie.get('userbelong');
            dataObj.audit_level = 0;
            dataObj.operation = 1;
            dataObj.source = this.nowSystemType;
            await tmpAddAPI(dataObj).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.createSt = false;
                    this.$warning({
                        title: '操作提示',
                        content: '请继续添加产品相关信息。',
                        okText: '知道了',
                        centered: true,
                        maskClosable: true
                    });
                    res.info.key = res.info.vendor_inner_sn + '' + res.info.product_inner_sn;
                    const curRow = this.$refs.xTable.getCurrentRow();
                    this.tableData.forEach((item, idx) => {
                        if (item.key === curRow.key) {
                            res.info.sourceName =
                                this.sourceName[parseFloat(res.info.data_source) || parseFloat(res.info.source)];
                            if (res.info.code68_sn) {
                                res.info.text_68 = res.info.code68_name + '(' + res.info.code68_sn + ')';
                            } else {
                                res.info.text_68 = '';
                            }
                            this.tableData.splice(idx, 0, res.info);
                            if (this.tableData.length + 1 > this.pageInit.pageSize) {
                                this.tableData.splice(this.tableData.length - 1, 1);
                            }
                            this.cellClickFn(res.info);
                            throw new Error('forEach循环结束');
                        } else if (this.tableData.length <= 0) {
                            this.tableData.push(res.info);
                            this.cellClickFn(res.info);
                        }
                    });
                    // this.loadTable(res.info)
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        // 编辑
        editGoodsFn() {
            this.$refs.editPage.returnPageData();
            // let oldData = this.chooseTrObj
            let newData = this.newEditDataObj;
            // let finalData = {}
            // let editInfoFlag = true
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

            // for (let server in newData) {
            //     let serverItem = newData[server]
            //     for (let old in oldData) {
            //         let oldItem = oldData[old]
            //         if (server === old && serverItem !== oldItem) {
            //             finalData = {
            //                 ...finalData,
            //                 [server]: serverItem, // 新值
            //                 [server + '_old']: oldItem // 旧值
            //             }
            //             editInfoFlag = false
            //         }
            //     }
            // }
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
            this.editProServe(newData);
        },
        async editProServe(data) {
            data.status = 2;
            let vid = this.chooseTrObj.vendor_inner_sn;
            let pid = this.chooseTrObj.product_inner_sn;
            data.key = this.chooseTrObj.key;
            data.org_id = this.$cookie.get('userbelong');
            data.vendor_inner_sn = vid;
            data.product_inner_sn = pid;
            data.operation = this.chooseTrObj.operation;
            if (!data.operation) {
                data.operation = 2;
            }
            data.audit_level = 0;
            data.source = this.nowSystemType;
            let nowApi;
            if (this.chooseTrObj.operation === 1) {
                nowApi = tmpComposeEditAPI;
            } else {
                nowApi = tmpEditAPI;
            }
            await nowApi(data).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.editSt = false;
                    res.info.key = res.info.vendor_inner_sn + '' + res.info.product_inner_sn;
                    if (res.info.code68_name) {
                        res.info.text_68 = res.info.code68_name + '(' + res.info.code68_sn + ')';
                    }
                    res.info.sourceName =
                        this.sourceName[parseFloat(res.info.data_source) || parseFloat(res.info.source)];
                    this.tableData.forEach((item, idx) => {
                        if (item.key === res.info.key) {
                            this.tableData.splice(idx, 1, res.info);
                            this.chooseTrObj = res.info;
                            this.cellClickFn(this.chooseTrObj);
                        }
                    });
                    // this.loadTable(res.info.key)
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
            let params = {
                org_id: this.$cookie.get('userbelong'),
                source: this.nowSystemType,
                vendor_inner_sn: vid,
                product_inner_sn: pid
            };
            await tmpDelAPI({ params }).then((res) => {
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
            await tmpListAPI({ params }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.pageInit.loading = false;
                    if (res.rows.length === 0) {
                        this.isChooseTr = false;
                        this.chooseTrObj = {};
                    }
                    this.tableData = res.rows.map((item) => {
                        item.health_care_sn = item.default_health_care_sn;
                        item.sun_sn = item.default_sun_sn;
                        item.isActive = '';
                        item.key = item.vendor_inner_sn + '' + item.product_inner_sn;
                        if (item.code68_name) {
                            item.text_68 = item.code68_name + '(' + item.code68_sn + ')';
                        }
                        item.sourceName = this.sourceName[parseFloat(item.data_source) || parseFloat(item.source)];
                        return item;
                    });
                    let total = res.total;
                    this.pageInit = {
                        ...this.pageInit,
                        total
                    };
                    // this.chooseTrObj = {}
                    // 清除缓存，等待新的数据返回
                    this.specData = [];
                    this.goodsImgList = [];
                    this.certImgList = [];
                    this.mdrfImgList = [];
                    this.examImgList = [];
                    this.mdrfData = [];

                    const xTable = this.$refs.xTable;
                    if (this.tableData.length > 0) {
                        if (chooseObj) {
                            // 选中新增/编辑的项
                            this.tableData.forEach((item) => {
                                if (item.key === this.chooseTrObj.key) {
                                    xTable.setCurrentRow(item); // 默认选中
                                    this.chooseTrObj = item;
                                    this.cellClickFn(item);
                                    this.chooseTrEvent(item);
                                }
                            });
                        } else {
                            const item = this.tableData[0];
                            xTable.setCurrentRow(item); // 默认选中第一
                            this.chooseTrObj = item;
                            this.cellClickFn(item);
                            this.chooseTrEvent(item);
                        }
                    } else {
                        this.specData = [];
                        this.goodsImgList = [];
                        this.examImgList = [];
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
        async sendCheck() {
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
            let vid = this.chooseTrObj.vendor_inner_sn;
            let pid = this.chooseTrObj.product_inner_sn;
            let params = {
                org_id: this.$cookie.get('userbelong'),
                source: this.nowSystemType,
                vendor_inner_sn: vid,
                product_inner_sn: pid,
                is_tmp: this.chooseTrObj.is_tmp
            };
            await tmpSendAuditAPI(params).then((res) => {
                if (parseFloat(res.code) === 0) {
                    // this.chooseTrObj = {}
                    // this.isChooseTr = false
                    this.$message.success('送审成功');
                    this.chooseTrObj.status = 3;
                    this.isSend = true;
                    this.loadTable(this.chooseTrObj);
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        openCreate() {
            this.createSt = true;
            this.openType = 'create';
            let cmp = this.$refs.createPage;
            if (cmp) {
                cmp.initPageData();
            }
            this.createDataObj = {};
            this.modalData = [];
            this.$Utils.globalDragCenterFn('create');
        },
        openEdit() {
            // 打开修改
            if (this.chooseTrObj.product_inner_sn) {
                // 赋值
                this.editSt = true;
                this.$cookie.set('pageType', 'proCheck', { expires: '1h' });
                if (this.chooseTrObj.operation === 1) {
                    this.$cookie.set('goodsType', 'deaCt', { expires: '1h' });
                }
                if (this.$refs.editPage) {
                    this.sendEditInfo();
                    this.$Utils.globalDragCenterFn('edit');
                } else {
                    this.$Utils.globalDragCenterFn('edit', true);
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
        closeEditGoods() {
            this.editSt = false;
            this.$cookie.set('pageType', '', { expires: '1h' });
            this.$cookie.set('goodsType', '', { expires: '1h' });
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
            this.editTrObj.specData = this.specData;
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
            let params = {
                org_id: this.$cookie.get('userbelong'),
                source: this.nowSystemType,
                vendor_inner_sn: obj.vendor_inner_sn,
                product_inner_sn: obj.product_inner_sn,
                image_inner_sn: obj.image_inner_sn,
                type: obj.type
            };
            await tmpImageDelAPI({ params }).then((res) => {
                if (parseInt(res.code) === 0) {
                    this.getGoodsImg();
                    this.getBizLicenseImg();
                    this.getProdLicenseImg();
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
                        org_id: this.$cookie.get('userbelong'),
                        source: this.nowSystemType,
                        operation: 1,
                        audit_level: 0,
                        status: 2,
                        vendor_inner_sn: this.chooseTrObj.vendor_inner_sn,
                        product_inner_sn: this.chooseTrObj.product_inner_sn,
                        type: 1,
                        url: url
                    };
                    await tmpImageAddAPI(params).then((res) => {
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
                    await tmpImageAddAPI(params).then((res) => {
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
        async getGoodsImg() {
            // 获取商品图片
            let vid = this.chooseTrObj.vendor_inner_sn;
            let pid = this.chooseTrObj.product_inner_sn;
            let params = {
                org_id: this.$cookie.get('userbelong'),
                source: this.nowSystemType,
                vendor_inner_sn: vid,
                product_inner_sn: pid
            };
            await tmpImageListAPI({ params }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.goodsImgList = res.images.map((item) => {
                        if (item.status === 3) {
                            this.isSend = true;
                        }
                        return item;
                    });
                } else {
                    // this.$message.error(res.msg)
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
        /** 注册证方法区 **/
        async getGicMdrf() {
            // 获取注册证登记表
            this.mdrfLoading = true;
            this.mdrfData = [];
            let vid = this.chooseTrObj.vendor_inner_sn;
            let pid = this.chooseTrObj.product_inner_sn;
            let params = {
                org_id: this.$cookie.get('userbelong'),
                source: this.nowSystemType,
                vendor_inner_sn: vid,
                product_inner_sn: pid
            };
            await tmpMdrfListAPI({ params }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    if (parseFloat(res.code) === 0) {
                        this.mdrfData = res.list.map((item) => {
                            item.key = item.mdrf_inner_sn;
                            item.isActive = '';
                            if (item.status === 3) {
                                this.isSend = true;
                            }
                            return item;
                        });

                        this.$refs.mdrfTable && this.$refs.mdrfTable[0].refreshColumn();
                    } else {
                        // this.$message.error(res.msg)
                    }
                    this.gicMdrfObj.name = this.chooseTrObj.name;
                    this.gicMdrfObj.vendor_name = this.chooseTrObj.vendor_name;
                    this.gicMdrfObj.address = this.chooseTrObj.vendor_address;
                    this.gicMdrfObj.specifications = this.chooseTrObj.specifications;
                    if (this.chooseTrMdrfObj !== {}) {
                        this.mdrfData.forEach((item) => {
                            if (item.mdrf_inner_sn === this.chooseTrMdrfObj.mdrf_inner_sn) {
                                item.isActive = 'active-tr';
                                setTimeout(() => {
                                    this.$refs.mdrfTable && this.$refs.mdrfTable[0].setCurrentRow(item); // 默认选中
                                }, 300);
                            } else {
                                item.isActive = '';
                            }
                        });
                    }
                } else {
                    // this.$message.error(res.msg)
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

            const timer = setTimeout(() => {
                this.$Utils.globalDragCenterFn('entryModal');
                this.$refs.mdrfGicPage.refreshPage();
                clearTimeout(timer);
            }, 0);
            // if (this.$refs.mdrfGicPage) {
            //     this.$Utils.globalDragCenterFn('entryModal')
            //     this.$refs.mdrfGicPage.refreshPage()
            // } else {
            //     this.$Utils.globalDragCenterFn('entryModal', true)
            // }
        },
        async editGicMdrf() {
            // 修改注册证
            this.mdrfIsNew = false;
            if (!this.chooseTrMdrfObj.vendor_inner_sn) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择注册证',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            const { vendor_inner_sn: vid, product_inner_sn: pid, mdrf_inner_sn: mid } = this.chooseTrMdrfObj;
            const params = {
                org_id: this.$cookie.get('userbelong'),
                source: this.nowSystemType,
                vendor_inner_sn: vid,
                product_inner_sn: pid,
                mdrf_inner_sn: mid
            };
            await tmpMdrfInfoAPI({ params }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.gicMdrfObj = {
                        ...res.info,
                        new: false,
                        vendor_inner_sn: vid
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
            let data = this.newMdrfGicData;
            data.org_id = this.$cookie.get('userbelong');
            data.product_inner_sn = pid;
            data.vendor_inner_sn = vid;
            data.source = this.nowSystemType;
            data.status = 2;
            data.audit_level = 0;
            if (this.mdrfIsNew) {
                // 新增
                data.operation = 1;
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
                // data.product_name_old = this.gicMdrfObj.name
                // data.specifications_old = this.gicMdrfObj.specifications
                await tmpMdrfAddAPI(data).then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.entrySt = false;
                        this.mdrfDelBtn = false;
                        this.chooseTrMdrfObj = res.info;
                        this.getGicMdrf();
                    } else {
                        this.$message.error(res.msg);
                    }
                });
            } else {
                // 修改
                let newData = data;
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
                data.mdrf_inner_sn = mid;
                data.operation = 2;
                await tmpMdrfEditAPI(data).then((res) => {
                    if (parseFloat(res.code) === 0) {
                        this.entrySt = false;
                        this.mdrfDelBtn = false;
                        this.getGicMdrf();
                    } else {
                        this.$message.error(res.msg);
                    }
                });
            }
        },
        delGicMdrf() {
            if (!this.chooseTrMdrfObj || this.chooseTrMdrfObj === {}) {
                this.$warning({
                    title: '操作提示',
                    content: '请选择注册证',
                    okText: '知道了',
                    centered: true,
                    maskClosable: true
                });
                return false;
            }
            const _this = this;
            _this.$confirm({
                title: '删除',
                content: '确认要执行该操作吗？',
                centered: true,
                maskClosable: false,
                onOk() {
                    _this.delGicMdrfFn();
                }
            });
        },
        async delGicMdrfFn() {
            let vid = this.chooseTrMdrfObj.vendor_inner_sn;
            let pid = this.chooseTrMdrfObj.product_inner_sn;
            let mid = this.chooseTrMdrfObj.mdrf_inner_sn;
            let params = {
                org_id: this.$cookie.get('userbelong'),
                source: this.nowSystemType,
                vendor_inner_sn: vid,
                product_inner_sn: pid,
                mdrf_inner_sn: mid
            };
            await tmpMdrfDelAPI({ params }).then((res) => {
                if (parseInt(res.code) === 0) {
                    this.chooseTrMdrfObj = {};
                    this.chooseTrMdrfObj = {};
                    this.loadTable(this.chooseTrObj);
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        chooseTrMdrf(id) {
            // 选中行渲染
            this.mdrfData.forEach((item) => {
                if (item.key === id) {
                    item.isActive = 'active-tr';
                } else {
                    item.isActive = '';
                }
            });
        },
        mdrfClickFn(row) {
            this.chooseTrMdrfObj = row.row || row;
            if (
                !this.chooseTrMdrfObj.status ||
                (parseInt(this.chooseTrMdrfObj.status) !== 2 && parseInt(this.chooseTrMdrfObj.status) !== 4)
            ) {
                this.mdrfDelBtn = true;
            } else {
                this.mdrfDelBtn = false;
            }
        },
        mrdfDblclickFn(row) {
            this.chooseTrMdrfObj = row.row || row;
            this.editGicMdrf();
            // if (!this.isSend) {
            //     this.editGicMdrf()
            // }
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
            this.specLoading = true;
            this.specData = [];
            let vid = this.chooseTrObj.vendor_inner_sn;
            let pid = this.chooseTrObj.product_inner_sn;
            let params = {
                org_id: this.$cookie.get('userbelong'),
                source: this.nowSystemType,
                vendor_inner_sn: vid,
                product_inner_sn: pid
            };
            await tmpSpecListAPI({ params }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    this.specData = res.list.map((item) => {
                        item.isActive = '';
                        item.key = item.specification_inner_sn;
                        if (item.status === 3) {
                            this.isSend = true;
                        }
                        return item;
                    });

                    // 默认选中表格中第一条数据
                    this.$nextTick(() => {
                        const specTable = this.$refs.specTable;
                        if (this.specData && this.specData.length) {
                            // this.chooseTrObj = this.specData[0]
                            this.chooseTrSpecObj = this.specData[0];
                            specTable.setCurrentRow(this.specData[0]);
                        } else {
                            specTable.clearCurrentRow();
                            this.chooseTrSpecObj = {};
                        }
                    });

                    if (chooseObj) {
                        this.chooseTrSpecObj = chooseObj;
                        let vid = this.chooseTrObj.vendor_inner_sn;
                        let pid = this.chooseTrObj.product_inner_sn;
                        let id = this.chooseTrSpecObj.specification_inner_sn;
                        let infoData = {
                            vid: vid,
                            pid: pid,
                            psid: id
                        };
                        this.packUnitObj = infoData;
                        this.chooseTrSpec(chooseObj.specification_inner_sn);
                    }
                } else {
                    // this.$message.error(res.msg)
                }
                this.specLoading = false;
            });
        },
        specClickFn(row) {
            this.chooseTrSpecObj = row.row || row;
            let vid = this.chooseTrObj.dealer_inner_sn;
            let pid = this.chooseTrObj.product_inner_sn;
            let id = this.chooseTrSpecObj.specification_inner_sn;
            let infoData = {
                vid: vid,
                pid: pid,
                psid: id
            };
            if (this.chooseTrSpecObj.status !== 2 && this.chooseTrSpecObj.status !== 4) {
                this.specDelBtn = true;
            } else {
                this.specDelBtn = false;
            }
            this.packUnitObj = infoData;
        },
        specDblclickFn(row) {
            this.chooseTrMdrfObj = row.row || row;
            this.openEditSpec();
            // if (!this.isSend) {
            //     this.openEditSpec()
            // }
        },
        chooseTrSpec(id) {
            // 选中行渲染
            this.specData.forEach((item) => {
                if (item.specification_inner_sn === id) {
                    item.isActive = 'active';
                    this.chooseTrSpecObj = item;
                    this.chooseTrObj = item;
                    setTimeout(() => {
                        this.$refs.specTable.setCurrentRow(item); // 默认选中
                    }, 300);
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
            if (this.chooseTrSpecObj.specification) {
                this.specTit = '修改规格型号';
                this.specSt = true;
                this.newSpecData.spec = this.chooseTrSpecObj.specification;
                this.newSpecData.pvd = this.chooseTrSpecObj.specification_vendor_sn;
                this.newSpecData.unit = this.chooseTrSpecObj.unit_name;
                this.newSpecData.healthCareSn = this.chooseTrSpecObj.default_health_care_sn;
                this.newSpecData.sunSn = this.chooseTrSpecObj.default_sun_sn;
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
                org_id: this.$cookie.get('userbelong'),
                source: this.nowSystemType,
                vendor_inner_sn: vid,
                product_inner_sn: pid,
                specifications: '',
                audit_level: 0,
                status_s: status,
                status: 2,
                unit_name: this.newSpecData.unit,
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
                params.specifications = spec;
                let pvd = this.$Utils.trim(this.newSpecData.pvd);
                if (pvd) {
                    this.specData.forEach((item) => {
                        if (item.specification_vendor_sn === pvd) {
                            sameSt = false;
                        }
                    });
                    if (!sameSt) {
                        this.$warning({
                            title: '操作提示',
                            content: '同一个产品的产品编码不可以重复',
                            okText: '知道了',
                            centered: true,
                            maskClosable: true
                        });
                        return false;
                    }
                    params.specification_vendor_sn = this.$Utils.trim(this.newSpecData.pvd);
                }
                params.operation = 1;
                await tmpSpecAddAPI(params).then((res) => {
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
                let sameSt = true;
                this.specData.forEach((item) => {
                    if (item.specification_inner_sn !== id && item.specification === spec) {
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
                params.specification = spec;
                params.specification_inner_sn = id;
                let pvd = this.$Utils.trim(this.newSpecData.pvd);
                if (pvd) {
                    this.specData.forEach((item) => {
                        if (item.specification_inner_sn !== id && item.specification_vendor_sn === pvd) {
                            sameSt = false;
                        }
                    });
                    if (!sameSt) {
                        this.$warning({
                            title: '操作提示',
                            content: '同一个产品的产品编码不可以重复',
                            okText: '知道了',
                            centered: true,
                            maskClosable: true
                        });
                        return false;
                    }
                    params.specification_vendor_sn = this.$Utils.trim(this.newSpecData.pvd);
                }
                if (this.chooseTrSpecObj.operation === null) {
                    params.operation = 2;
                } else {
                    params.operation = this.chooseTrSpecObj.operation;
                }
                await tmpSpecEditAPI(params).then((res) => {
                    if (parseFloat(res.code) === 0) {
                        // this.specDelBtn = false
                        // this.specData.forEach(item => {
                        //     if (item.specification_inner_sn === params.specification_inner_sn) {
                        //         item.unit_name = params.unit_name
                        //         item.specification = params.specification
                        //         item.specification_vendor_sn = params.specification_vendor_sn
                        //         item.status = 2
                        //     }
                        // })
                        this.chooseTrObj.tmpSpecList = this.specData;
                        // this.loadSpecFn(this.chooseTrSpecObj)
                        // 临时更新表格数据
                        const specTable = this.$refs.specTable,
                            defaultCurRow = specTable.getCurrentRecord(),
                            defaultCurRowIndex = specTable.getRowIndex(defaultCurRow),
                            curEditData = {
                                ...defaultCurRow,
                                ...params,
                                spec: params.specification,
                                specification: params.specification,
                                specification_vendor_sn: params.specification_vendor_sn,
                                status: params.status
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
            let params = {
                org_id: this.$cookie.get('userbelong'),
                source: this.nowSystemType,
                vendor_inner_sn: vid,
                product_inner_sn: pid,
                specification_inner_sn: id
            };
            await tmpSpecDelAPI({ params }).then((res) => {
                if (parseFloat(res.code) === 0) {
                    // this.loadSpecFn()
                    this.specData.forEach((item, idx) => {
                        if (item.specification_inner_sn === id) {
                            this.specData.splice(idx, 1);
                        }
                    });
                    this.chooseTrObj.tmpSpecList = this.specData;
                    this.loadSpecFn();
                    this.chooseTrSpecObj = {};
                } else {
                    this.$message.error(res.msg);
                }
            });
        },
        openAudit(res) {
            this.$warning({
                title: '不通过原因',
                content: res,
                okText: '知道了',
                centered: true,
                maskClosable: true
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
        /*****************************************************
         * 测试代码
         */
        handleChange(value) {
            this.newSpecData.healthCareSn = value;
        },
        filterOption(input, option) {
            this.newSpecData.healthCareSn = input;
            return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0;
        },
        /*****************************************************/
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
        }
    },
    watch: {
        highSearch(val) {
            // if (val) {
            //     this.tabScrollHeight = this.tabScrollHeight - 38
            // } else {
            //     this.tabScrollHeight = this.tabScrollHeight + 38
            // }
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
    created() {
        // 医院端 -> 1
        // 经销商端 -> 2
        this.nowSystemType = parseInt(this.$cookie.get('userSystemType'));
        this.nowOid = parseInt(this.$cookie.get('userbelong'));
    },
    mounted() {
        if (this.nowSystemType === 3) {
            this.columns.splice(1, 1);
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
.img_sp {
    background: #b3b3b3;
    color: #fff;
    position: absolute;
    bottom: 10px;
    right: 10px;
    .yellow {
        border-radius: 5px;
        padding: 0 4px;
        background: #d37200;
        border: 1px solid #d37200;
    }
    .gray {
        border-radius: 5px;
        padding: 0 4px;
        background: #a3a3a3;
        border: 1px solid #a3a3a3;
    }
    .red {
        border-radius: 5px;
        padding: 0 4px;
        color: #fff !important;
        background: red;
        border: 1px solid red;
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
    cursor: n-resize;
    cursor: e-resize;
    float: left;
    margin-left: 5px;
    border-left: 1px solid #e8e8e8;
    border-right: 1px solid #e8e8e8;
}
.boxdown {
    float: left;
    width: 100%;
    height: 50%;
}
.ant-select-selection-selected-value {
    display: block !important;
}
.specTable .vxe-table--border-line {
    border: none !important;
}
</style>
