package com.yrt.project.api.external.warehouse;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yrt.common.exception.BusinessException;
import com.yrt.project.modular.external.mapper.HisInterfaceLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.yrt.common.client.HttpAPIService;
import com.yrt.common.client.HttpResult;
import com.yrt.common.client.ResultBody;
import com.yrt.common.dict.SystemType;
import com.yrt.common.dict.VoucherType;
import com.yrt.common.utils.Utils;
import com.yrt.project.api.external.vo.ExternalSendBody;
import com.yrt.project.modular.external.domain.HisInterface;
import com.yrt.project.modular.external.domain.HisInterfaceLog;
import com.yrt.project.modular.external.mapper.HisInterfaceMapper;
import com.yrt.project.modular.warehouse.domain.Grn4External;
import com.yrt.project.modular.warehouse.domain.Odo4External;

@Service
@Slf4j
public class SyncWarehouseService {

    @Autowired
    HttpAPIService service;
    @Autowired
    HisInterfaceLogMapper hisInterfaceLogMapper;
    @Autowired
    HisInterfaceMapper hisInterfaceMapper;

    @Value("${checkhis.flag}")
    private Boolean checkhisFlag;

    public String sendPutout(Long orgId, SystemType type, Odo4External odo) {
        HisInterface his = getInterface(orgId, type, VoucherType.PUT_OUT);
        if (Utils.isEmpty(his)){
            return "";
        }
        ExternalSendBody sendBody = ExternalSendBody.of("putout", odo);
        String body = JSONObject.toJSONString(sendBody);
        HisInterfaceLog hisInterfaceLog = new HisInterfaceLog();
        hisInterfaceLog.setOrganizationInnerSn(his.getOrganizationInnerSn());
        hisInterfaceLog.setOrganizationType(his.getOrganizationType());
        hisInterfaceLog.setVoucherType(his.getVoucherType());
        hisInterfaceLog.setUrl(his.getUrl());
        hisInterfaceLog.setMessageS(body);
        hisInterfaceLog.setTimeS(new Date());
        hisInterfaceLogMapper.insert(hisInterfaceLog);

        CloseableHttpResponse response;
        try {
            response = send(his, body);
        } catch (Exception e) {
            log.error("同步HIS异常", e);
            throw new BusinessException("同步HIS库存异常，可能是网络原因。请重试！");
        }

        String respStr;
        try {
            respStr = EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (IOException e) {
            throw new BusinessException("转换HIS返回报文异常！");
        }
        hisInterfaceLog.setTimeR(new Date());
        hisInterfaceLog.setMessageR(respStr);
        hisInterfaceLogMapper.updateById(hisInterfaceLog);

        int code = response.getStatusLine().getStatusCode();
        if (code != 200) {
            throw new BusinessException("同步HIS库存异常，请联系开发人员！");
        }

        ResultBody resultBody;
        try {
            resultBody = JSONObject.parseObject(respStr, ResultBody.class);
        } catch (Exception ex) {
            throw new BusinessException("同步HIS库存异常，HIS返回报文非标准报文！");
        }

        if (resultBody.getResult() == null || resultBody.getResult() != 0) {
            String respMsg = resultBody.getResult_msg();
            String msg = "HIS系统出库出错：\\n" + respMsg == null ? "" : respMsg;
            if (checkhisFlag != null && checkhisFlag) {
                throw new BusinessException(msg);
            }
        }

        return "";
    }

    public String sendPutin(Long orgId, SystemType type, Grn4External grn) {
        HisInterface his = getInterface(orgId, type, VoucherType.PUT_IN);
        if (Utils.isEmpty(his)) return "";


        ExternalSendBody sendBody = ExternalSendBody.of("putin", grn);
        String body = JSONObject.toJSONString(sendBody);
        HisInterfaceLog hisInterfaceLog = new HisInterfaceLog();
        hisInterfaceLog.setOrganizationInnerSn(his.getOrganizationInnerSn().intValue());
        hisInterfaceLog.setOrganizationType(his.getOrganizationType());
        hisInterfaceLog.setVoucherType(his.getVoucherType());
        hisInterfaceLog.setUrl(his.getUrl());
        hisInterfaceLog.setMessageS(body);
        hisInterfaceLog.setTimeS(new Date());
        hisInterfaceLogMapper.insert(hisInterfaceLog);
        CloseableHttpResponse response;
        try {
            response = send(his, body);
        } catch (Exception e) {
            log.error("同步HIS异常", e);
            throw new BusinessException("同步HIS异常，可能是网络原因。请重试！");
        }

        String respStr;
        try {
            respStr = EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (IOException e) {
            throw new BusinessException("转换HIS返回报文异常！");
        }
        hisInterfaceLog.setTimeR(new Date());
        hisInterfaceLog.setMessageR(respStr);
        hisInterfaceLogMapper.updateById(hisInterfaceLog);

        int code = response.getStatusLine().getStatusCode();
        if (code != 200) {
            throw new BusinessException("同步HIS库存异常，请联系开发人员！");
        }

        ResultBody resultBody;
        try {
            resultBody = JSONObject.parseObject(respStr, ResultBody.class);
        } catch (Exception ex) {
            throw new BusinessException("同步HIS库存异常，HIS返回报文非标准报文！");
        }

        if (resultBody.getResult() == null || resultBody.getResult() != 0) {
            String respMsg = resultBody.getResult_msg();
            String msg = "HIS系统入库出错：\\n" + respMsg == null ? "" : respMsg;
            if (checkhisFlag != null && checkhisFlag) {
                throw new BusinessException(msg);
            }
        }
        return "";
    }

    private HisInterface getInterface(Long orgId, SystemType systemType, VoucherType type) {
        Map<String, Object> params = new HashMap<>();
        params.put("orgId", orgId);
        params.put("systemType", systemType.value());
        params.put("voucherType", type.value());

        QueryWrapper<HisInterface> qw = new QueryWrapper();
        qw.eq(HisInterface.COL_ORGANIZATION_INNER_SN, orgId)
                .eq(HisInterface.COL_ORGANIZATION_TYPE,systemType.value())
                .eq(HisInterface.COL_VOUCHER_TYPE, type.value());

        HisInterface his = hisInterfaceMapper.selectOne(qw);
        if (Utils.isEmpty(his)) {
            return null;
        }
        return his;
    }

    private CloseableHttpResponse send(HisInterface his, String body) throws Exception {
        try {
            return service.doPostWithJson(his.getUrl(), body);
        } catch (Exception e) {
            throw e;
        }
    }
}
