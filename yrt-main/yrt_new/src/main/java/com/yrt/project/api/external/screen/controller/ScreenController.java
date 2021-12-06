package com.yrt.project.api.external.screen.controller;

import com.yrt.framework.web.controller.BaseController;
import com.yrt.framework.web.domain.ResultInfo;
import com.yrt.framework.web.page.TableDataInfo;
import com.yrt.project.api.external.screen.dto.*;
import com.yrt.project.api.external.screen.service.ScreenService;
import com.yrt.project.api.external.screen.vo.*;
import com.yrt.project.modular.organization.domain.Company;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/business/screen")
@Api(tags = "业务-大屏数据接入")
public class ScreenController extends BaseController {

    @Autowired
    ScreenService service;

    @ApiOperation(value = "大屏数据首页", notes = "需传入分页条件pageNum,pageSize")
    @GetMapping("/")
    public ResultInfo<ScreenData> screen(){
        return ResultInfo.success(service.screen());
    }

    @ApiOperation(value = "机构数据省级区域统计展示", notes = "需传入分页条件pageNum,pageSize")
    @GetMapping("/orgs")
    public ResultInfo<OrgData> orgs(@ApiParam(name = "administrative_division_sn_province", value = "省份行政区划代码", required = true) Long administrative_division_sn_province){
        return ResultInfo.success(service.orgs(administrative_division_sn_province));
    }

    @SuppressWarnings("unchecked")
    @ApiOperation(value = "机构数据市级区域列表展示", notes = "需传入分页条件pageNum,pageSize")
    @GetMapping("/orgs/{type}")
    public TableDataInfo<Company> orgs(@ApiParam(name = "type", value = "机构类型 1=医院 2=经销商 3=厂商", required = true) @PathVariable int type,
                                       @ApiParam(name = "administrative_division_sn_city", value = "市级行政区划代码", required = true) Long administrative_division_sn_city){
        startPage();
        return getDataTable(service.orgsByTypeAndAdministrative_division_sn(type,administrative_division_sn_city));
    }

    @SuppressWarnings("unchecked")
    @ApiOperation(value = "机构预警信息（分页）")
    @GetMapping("/warns/orgs")
    public TableDataInfo<OrgWarnInfo> orgWarns(@Validated SearchWarnForm form){
        startPage();
        return getDataTable(service.orgWarns(form));
    }

    @SuppressWarnings("unchecked")
    @ApiOperation(value = "产品预警信息（分页）")
    @GetMapping("/warns/products")
    public TableDataInfo<ProductWarnInfo> productWarns(@Validated SearchWarnForm form){
        startPage();
        return getDataTable(service.productWarns(form));
    }

    @ApiOperation(value = "机构信息", notes = "")
    @GetMapping("/orgs/info/{type}")
    public ResultInfo<Map<String,Object>> orgs(@ApiParam(name = "type", value = "机构类型 1=医院 2=经销商 3=厂商", required = true) @PathVariable Integer type,
                                   @ApiParam(name = "orgName", value = "机构名字", required = true) String orgName){
        return ResultInfo.success(service.orgInfo(type,orgName));
    }

    @ApiOperation(value = "产品信息", notes = "")
    @GetMapping("/products/info")
    public ResultInfo<Map<String,Object>> orgs(@Validated SearchProductForm form){
        return ResultInfo.success(service.productInfo(form));
    }

    @ApiOperation(value = "供应链", notes = "")
    @GetMapping("/scm/{type}/{oid}")
    public ResultInfo<Map<String, List<OrgInfo>>> supplyChain(@ApiParam(name = "type", value = "机构类型 1-医院 2-经销商 3-厂商", required = true) @PathVariable("type") int type,
        @ApiParam(name = "oid", value = "机构id", required = true) @PathVariable("oid") Long oid){
        return ResultInfo.success(service.supplyChain(type,oid));
    }

    @ApiOperation(value = "科室", notes = "只展示第一级科室")
    @GetMapping("/scm/hospital/depts")
    public ResultInfo<List<DeptInfo>> depts(@ApiParam(name = "oid", value = "医院id", required = true) Long oid){
        return ResultInfo.success(service.deptsLevel1(oid));
    }

    @SuppressWarnings("unchecked")
    @ApiOperation(value = "子页面:产品交易统计", notes = "分页查询")
    @GetMapping("/trade/{orgType}/{orgId}")
    public TableDataInfo<ProductTradeInfo> trades(@PathVariable int orgType,@PathVariable long orgId){
        startPage();
        return getDataTable(service.trades(orgType,orgId));
    }

    @SuppressWarnings("unchecked")
    @ApiOperation(value = "子页面:产品交易统计", notes = "分页查询,不传机构id直接按区域检索")
    @GetMapping("/trade/items")
    public TableDataInfo<ProductTradeInfo> tradesX(
            @ApiParam(name = "administrative_division_sn_province", value = "省份行政区划代码") Long administrative_division_sn_province,
            @ApiParam(name = "administrative_division_sn_city", value = "市级行政区划代码") Long administrative_division_sn_city){
        startPage();
        return getDataTable(service.tradesX(administrative_division_sn_province,administrative_division_sn_city));
    }

    @ApiOperation(value = "子页面:产品交易详情", notes = "包含产品交易相关属性和统计属性")
    @GetMapping("/product/info/{vd_inner_sn}/{product_inner_sn}")
    public ResultInfo<ProductTotal> productTotal(@PathVariable long vd_inner_sn, @PathVariable long product_inner_sn){
        return ResultInfo.success(service.getProductTotal(vd_inner_sn,product_inner_sn));
    }

}
