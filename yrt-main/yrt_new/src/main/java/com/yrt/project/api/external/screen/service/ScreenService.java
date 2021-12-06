package com.yrt.project.api.external.screen.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yrt.common.exception.ValidateException;
import com.yrt.common.utils.StringUtils;
import com.yrt.common.utils.sql.SqlUtil;
import com.yrt.framework.web.page.PageDomain;
import com.yrt.framework.web.page.TableDataInfo;
import com.yrt.framework.web.page.TableSupport;
import com.yrt.project.api.external.screen.dto.*;
import com.yrt.project.api.external.screen.vo.*;
import com.yrt.project.modular.external.mapper.ScreenMapper;
import com.yrt.project.modular.organization.domain.Company;
import com.yrt.project.modular.organization.domain.OrgRelImage;
import com.yrt.project.modular.organization.mapper.DealerMapper;
import com.yrt.project.modular.organization.mapper.HospitalMapper;
import com.yrt.project.modular.organization.mapper.VendorMapper;
import com.yrt.project.modular.product.mapper.DealerProductUnitMapper;
import com.yrt.project.modular.product.mapper.ProductUnitMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.geom.FlatteningPathIterator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class ScreenService {

    @Autowired ScreenMapper mapper;
    @Autowired HospitalMapper hospitalMapper;
    @Autowired DealerMapper dealerMapper;
    @Autowired VendorMapper vendorMapper;

    @Autowired
    ProductUnitMapper pMapper;
    @Autowired
    DealerProductUnitMapper dpMapper;

    protected void startPage()
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize))
        {
            String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
            PageHelper.startPage(pageNum, pageSize, orderBy);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected TableDataInfo getDataTable(List<?> list)
    {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(0);
        PageInfo pageInfo = new PageInfo(list);
        if(pageInfo.getPageNum() > pageInfo.getPages()) {
            rspData.setRows(new ArrayList<>());
        }else {
            rspData.setRows(list);
        }
        rspData.setTotal(pageInfo.getTotal());

        return rspData;
    }

    @SuppressWarnings("unchecked")
    public ScreenData screen(){
        //中间 机构数量统计
        List<OrgTypeCount> orgTypeQuantities = mapper.searchOrgs();
        Map<Integer, BigDecimal> orgTypeMap = orgTypeQuantities.stream()
                .collect(Collectors.groupingBy(OrgTypeCount::getType, Collectors.reducing(BigDecimal.ZERO, OrgTypeCount::getCount, BigDecimal::add)));

        Map<OrgCount, BigDecimal> orgCountMap = orgTypeQuantities.stream()
                .collect(Collectors.groupingBy(x->new OrgCount(BigDecimal.ZERO,x.getArea(),x.getAdministrative_division_sn()), Collectors.reducing(BigDecimal.ZERO, OrgTypeCount::getCount, BigDecimal::add)));

        //手动添加回收商、科室
        orgTypeMap.put(4,BigDecimal.ZERO);  //回收商
        orgTypeMap.put(5,mapper.searchGospitalDeptTotal());    //科室总数

        List<OrgType> otList=new ArrayList<>();
        orgTypeMap.forEach((k,v)->otList.add(new OrgType(k,v)));

        List<OrgCount> ocList=new ArrayList<>();
        orgCountMap.forEach((k,v)->{
            k.setCount(v);
            ocList.add(k);
        });

        OrgData orgData = new OrgData(otList,ocList);

        //右上 机构预警
        List<OrgWarn> orgWarns = mapper.searchOrgWarn();
        orgWarns.add(new OrgWarn(4, BigDecimal.ZERO));
        orgWarns.add(new OrgWarn(5, BigDecimal.ZERO));

        TableDataInfo<OrgWarnInfo> orgWarnInfoPage;
        {
            startPage();
            orgWarnInfoPage = getDataTable(mapper.searchOrgWarnInfos());
        }

        //右下 产品预警
        List<ProductWarn> productWarns =mapper.searchProductWarn();
        TableDataInfo<ProductWarnInfo> productWarnInfoPage;
        {
            startPage();
            productWarnInfoPage=getDataTable(mapper.searchProductWarnInfos());
        }

        //左上，产品类型统计
        List<ProductTypeQuantity> productTypeQuantities=mapper.searchProductType();

        //左下，交易统计(日交易、月交易、交易项)
        //日交易统计
        List<TradeData> dayTrade =getTrade(IntStream.range(1,7).mapToObj(x -> LocalDate.now().plusDays(-x)).collect(Collectors.toList()),0);

        //月交易统计
        List<TradeData> monthTrade =getTrade(IntStream.range(1,7).mapToObj(x -> LocalDate.now().minusMonths(x)).collect(Collectors.toList()),1);

        //交易项
        List<ProductTradeInfo> items;
        {
            startPage();
            items=mapper.searchTradeItems();
        }

        ProductTradeData productTradeData = new ProductTradeData(dayTrade,monthTrade,items);

        return new ScreenData(productTypeQuantities,productTradeData, orgData, new OrgWarnData(orgWarns, orgWarnInfoPage), new ProductWarnData(productWarns, productWarnInfoPage));
    }

    private List<TradeData> getTrade(List<LocalDate> times, int flag){
        Map<String,Object> params=new HashMap<>(2);
        params.put("flag",flag);
        params.put("times",times);
        List<ProductTrade> trades = mapper.searchTrade(params);
        Map<String, Map<String, Long>> map = trades.stream().collect(Collectors.groupingBy(ProductTrade::getType, Collectors.groupingBy(ProductTrade::getDate, Collectors.reducing(0L, ProductTrade::getAmount, Long::sum))));
        List<TradeData> data=new ArrayList<>();
        map.forEach((k,v)->{
            List<ProductTrade> pts=new ArrayList<>();
            v.forEach((x,y)->pts.add(new ProductTrade(k,y,x,null,null,null,null,BigDecimal.ZERO,null)));
            pts.sort(Comparator.comparing(ProductTrade::getDate));
            data.add(new TradeData(k,pts));
        });
        return data;
    }

    public List<OrgWarnInfo> orgWarns(SearchWarnForm form) {
        Map<String,Object> params=new HashMap<>();
        params.put("province", form.getAdministrative_division_sn_province());
        params.put("city",form.getAdministrative_division_sn_city());
        params.put("content",form.getContent());
        return mapper.searchOrgWarnInfoPage(params);
    }

    public List<ProductWarnInfo> productWarns(SearchWarnForm form) {
        Map<String,Object> params=new HashMap<>();
        params.put("content",form.getContent());
        params.put("type",form.getType());
        return mapper.searchProductWarnInfoPage(params);
    }

    public OrgData orgs(Long administrative_division_sn_province) {
        List<OrgTypeCount> orgTypeQuantities = mapper.searchOrgsByAdministrative_division_sn(administrative_division_sn_province);

        Map<Integer, BigDecimal> orgTypeMap = orgTypeQuantities.stream()
                .collect(Collectors.groupingBy(OrgTypeCount::getType, Collectors.reducing(BigDecimal.ZERO, OrgTypeCount::getCount, BigDecimal::add)));

        Map<OrgCount, BigDecimal> orgCityMap = orgTypeQuantities.stream()
                .collect(Collectors.groupingBy(x->new OrgCount(BigDecimal.ZERO,x.getArea(),x.getAdministrative_division_sn()), Collectors.reducing(BigDecimal.ZERO, OrgTypeCount::getCount, BigDecimal::add)));

        //手动添加回收商、科室
        orgTypeMap.put(4,BigDecimal.ZERO);  //回收商
        orgTypeMap.put(5,mapper.searchGospitalDeptTotal());    //科室总数

        List<OrgType> otList=new ArrayList<>();
        orgTypeMap.forEach((k,v)->otList.add(new OrgType(k,v)));

        List<OrgCount> ocList=new ArrayList<>();
        orgCityMap.forEach((k,v)->{
            k.setCount(v);
            ocList.add(k);
        });

        return new OrgData(otList,ocList);
    }

    public List<Company> orgsByTypeAndAdministrative_division_sn(int type, Long administrative_division_sn_city) {
        if (type<1 || type>3) type=1;
        return mapper.searchOrgsByTypeAndAdministrative_division_sn(type,administrative_division_sn_city);
    }

    public Map<String,Object> orgInfo(Integer type, String orgName) {
        if (type<1||type>3) throw new ValidateException("机构类型错误");
        Map<String,Object> map=new HashMap<>();
        map.put("name",orgName);
        Company org=null;
        List<OrgRelImage> images=null;
        switch (type){
            case 1:
                org=hospitalMapper.searchOrgList(map).get(0);
                images=hospitalMapper.selectCertImageList(org.getId());
                break;
            case 2:
                org=dealerMapper.searchOrgList(map).get(0);
                images=dealerMapper.selectCertImageList(org.getId());
                break;
            case 3:
                org=vendorMapper.searchOrgList(map).get(0);
                images=vendorMapper.selectCertImageList(org.getId());
                break;
        }
        map.put("org",org);
        map.put("images",images);
        return map;
    }

    public Map<String, Object> productInfo(SearchProductForm form) {
        Map<String,Object> map=new HashMap<>();
        map.put("product_inner_sn",form.getProduct_inner_sn());
        map.put("vendor_inner_sn",form.getVendor_inner_sn());
        map.put("product",mapper.searchProduct(map));
        map.put("images",mapper.searchProductImages(map));
        return map;
    }

    public Map<String, List<OrgInfo>> supplyChain(int type, Long oid) {
        Map<String, List<OrgInfo>> chain=new HashMap<>();
        List<OrgInfo> upstream=null;
        List<OrgInfo> downstream=null;
        if (1==type){
            //医院，上游机构(厂商、经销商、医院自己)
            upstream=mapper.searchHospitalUpstream(oid);
        }else if (3==type){
            //厂商，下游机构(经销商、医院)
            downstream=mapper.searchVendorDownstream(oid);
        }else {
            //经销商，上游(厂商、经销商)、下游(经销商、医院)
            upstream=mapper.searchDealerUpstream(oid);
            downstream=mapper.searchDealerDownstream(oid);
        }
        chain.put("upstream",upstream);
        chain.put("downstream",downstream);
        return chain;
    }

    public List<DeptInfo> deptsLevel1(Long oid) {
        return mapper.searchDeptsLevel1(oid);
    }

    public List<ProductTradeInfo> trades(int type,long orgId) {
        switch (type){
            case 3:
                return mapper.searchVendorTradeItems(orgId);
            case 2:
                return mapper.searchDealerTradeItems(orgId);
            default:
                return mapper.searchHospitalTradeItems(orgId);
        }
    }

    public List<ProductTradeInfo> tradesX(Long administrative_division_sn_province, Long administrative_division_sn_city) {
        if (Objects.isNull(administrative_division_sn_province)&&Objects.nonNull(administrative_division_sn_city))
            return new ArrayList<>();
        Long adSn=Optional.ofNullable(administrative_division_sn_city).orElse(administrative_division_sn_province);
        return mapper.searchTradeItemsByAdSn(adSn);
    }

    public ProductTotal getProductTotal(long vd_inner_sn, long product_inner_sn) {
        //查销售额、批号总数
        ProductTotal productTotal=mapper.getProductTotal(vd_inner_sn,product_inner_sn);

        //经销商总数
        long dealerCount=mapper.getTradeDealerCount(vd_inner_sn,product_inner_sn);

        //使用科室总数
        long deptCount=mapper.getDeptCount(vd_inner_sn,product_inner_sn);

        //图片url
        String imgUrl=mapper.getImgUrl(vd_inner_sn,product_inner_sn);

        productTotal.setDealerCount(dealerCount);
        productTotal.setUseDeptCount(dealerCount);
        productTotal.setImgUrl(imgUrl);

        return productTotal;
    }
}
