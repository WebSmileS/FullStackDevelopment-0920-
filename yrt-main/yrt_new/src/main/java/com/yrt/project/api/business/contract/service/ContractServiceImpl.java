package com.yrt.project.api.business.contract.service;

import java.math.BigDecimal;
import java.util.*;

import com.yrt.project.modular.warehouse.mapper.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.yrt.common.dict.BillsType;
import com.yrt.common.dict.SystemType;
import com.yrt.common.dict.VoucherType;
import com.yrt.common.exception.DBException;
import com.yrt.common.exception.DuplicateException;
import com.yrt.common.utils.MessageUtils;
import com.yrt.common.utils.ServletUtils;
import com.yrt.common.utils.StringUtils;
import com.yrt.common.utils.Utils;
import com.yrt.project.api.business.contract.vo.*;
import com.yrt.project.api.common.service.VoucherNumberService;
import com.yrt.project.modular.contract.domain.*;
import com.yrt.project.modular.contract.mapper.*;
import com.yrt.project.modular.organization.domain.OrgEmployee;
import com.yrt.project.modular.organization.mapper.DealerMapper;
import com.yrt.project.modular.organization.mapper.HospitalMapper;
import com.yrt.project.modular.organization.mapper.VendorMapper;
import com.yrt.project.modular.product.mapper.*;

@Service
public class ContractServiceImpl implements IContractService {

	@Autowired private VoucherNumberService numberUtils;
	
	@Autowired HospitalMapper hMapper;
	@Autowired DealerMapper dMapper;
	@Autowired VendorMapper vMapper;
	
	@Autowired DealerDealerContractMapper ddMapper;
	@Autowired DealerVendorContractMapper dvMapper;
	@Autowired HospitalDealerContractMapper hdMapper;
	@Autowired HospitalVendorContractMapper hvMapper;
	
	private String getCurUserEmployeeName() {
		SystemType systemType = ServletUtils.getCurUserSystemType();
		OrgEmployee curEmp = null;
		String curEmpName = "";
		switch (systemType) {
		case HOSPITAL:
			curEmp = hMapper.selectOrgEmployeeInfoByUserId(ServletUtils.getCurUserID());
			break;
		case DEALER:
			curEmp = dMapper.selectOrgEmployeeInfoByUserId(ServletUtils.getCurUserID());
			break;
		case VENDOR:
			curEmp = vMapper.selectOrgEmployeeInfoByUserId(ServletUtils.getCurUserID());
			break;
		default:
		}
		if (curEmp != null) {
			curEmpName = curEmp.getName();
		}
		return curEmpName;
	}
	
	private ContractMapper getMapper(Integer contract_type) {
		switch (contract_type) {
		case 0://hospitalDealer
			return hdMapper;
		case 1://hospitalVendor
			return hvMapper;
		case 2://dealerVendor
			return dvMapper;
		case 3://dealerDealer
			return ddMapper;
		default:
			break;
		}
		throw new DBException("错误的合同类型!");
	}


	@Autowired
	private DealerDealerOdoMapper ddOdoMapper;
	@Autowired
	private DealerHospitalOdoMapper dhOdoMapper;
	@Autowired
	private DealerVendorOdoMapper dvOdoMapper;
	@Autowired
	private HospitalHospitalOdoMapper hhOdoMapper;
	@Autowired
	private HospitalDealerOdoMapper hdOdoMapper;
	@Autowired
	private HospitalVendorOdoMapper hvOdoMapper;
	@Autowired
	private VendorDealerOdoMapper vdOdoMapper;
	@Autowired
	private VendorHospitalOdoMapper vhOdoMapper;

	private OdoMapper getOdoMapper(BillsType billsType) {
		switch (billsType) {
			case DD:
				return ddOdoMapper;
			case DV:
				return dvOdoMapper;
			case DH:
				return dhOdoMapper;
			case HH:
				return hhOdoMapper;
			case HD:
				return hdOdoMapper;
			case HV:
				return hvOdoMapper;
			case VD:
				return vdOdoMapper;
			case VH:
				return vhOdoMapper;
			case VV:
		}
		throw new DBException("当前机构不支持该功能!");
	}

	@Override
	@Transactional
	public Contract addContract(AddContractForm form, int status) { 
		ContractMapper mapper = getMapper(form.getContract_type());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("partA_i", form.getPartA());
		params.put("partB_i", form.getPartB());
		params.put("sale_representative_inner_sn_i", null);//2021-03-29应采购要求取消选择人员，改为手填名称 StringUtils.isNull(form.getEmployee_inner_sn())?null:form.getEmployee_inner_sn());
		params.put("sale_representative_name_i", StringUtils.isEmpty(form.getEmployee_name())?"":form.getEmployee_name());
		params.put("inputer_inner_sn_i", ServletUtils.getCurUserID());
		params.put("payment_period_i", form.getPayment_period());
		params.put("begin_date_i", form.getBegin_date());
		params.put("end_date_i", form.getEnd_date());
		params.put("sign_date_i", form.getSign_date());
		params.put("name_i", form.getName());
		if(StringUtils.isEmpty(form.getContract_sn())) {
			params.put("contract_sn_i", numberUtils.getMaxSn(VoucherType.CONTRACT, SystemType.HOSPITAL, form.getPartA()));
		}else {
			params.put("contract_sn_i", form.getContract_sn());
		}
		params.put("status_i", status);
		params.put("inputer_name_i", getCurUserEmployeeName());
		if (form.getContent() == null) {
			params.put("content_i", "");
		} else {
			params.put("content_i", form.getContent());
		}
		mapper.addContract(params);
		if (Integer.valueOf(params.get("result_o").toString()) == 0) {
			Long cid = Long.valueOf(params.get("contract_inner_sn_o").toString());
			// 商品
			if(mapper instanceof HospitalDealerContractMapper || mapper instanceof HospitalVendorContractMapper) {
				if (StringUtils.isNotEmpty(form.getHospital_department_inner_sn())) {
					String[] dept_id = form.getHospital_department_inner_sn().split(",");
					for (String dept_innser_sn : dept_id) {
						Map<String, Object> params1 = new HashMap<>();
						params1.put("partA", form.getPartA());
						params1.put("partB", form.getPartB());
						params1.put("contract_inner_sn", cid);
						params1.put("department_inner_sn", Long.valueOf(dept_innser_sn));
						mapper.insertCRelHD(params1);
					}
				}
			}

			// 附件
			Map<String, Object> params2 = new HashMap<String, Object>();
			List<ContractProductForm> list = form.getProductList();
			for (ContractProductForm pform : list) {
				params2.put("partA", form.getPartA());
				params2.put("partB", form.getPartB());
				params2.put("product_type", pform.getProduct_type());
				params2.put("product_vendor_inner_sn", pform.getProduct_vendor_inner_sn());
				params2.put("product_vendor_name", pform.getProduct_vendor_name());
				params2.put("product_inner_sn", pform.getProduct_inner_sn());
				params2.put("product_name", pform.getProduct_name());
				params2.put("specification_inner_sn", pform.getSpecification_inner_sn());
				params2.put("specification", pform.getSpecification());
				params2.put("unit_inner_sn", pform.getUnit_inner_sn());
				params2.put("unit_name", pform.getUnit_name());
				params2.put("price", pform.getPrice().multiply(new BigDecimal(10000)));
				params2.put("contract_inner_sn", cid);
				if (pform.getProduct_type() == 1) {
					mapper.insertContractProduct(params2);
				} else {
					mapper.insertContractDealerProduct(params2);
				}
			}

			Map<String, Object> params3 = new HashMap<String, Object>();
			List<ContractAttachmentForm> list2 = form.getAttachmentList();
			for (ContractAttachmentForm form2 : list2) {
				params3.put("partA_i", form.getPartA());
				params3.put("partB_i", form.getPartB());
				params3.put("contract_inner_sn_i", Long.valueOf(params.get("contract_inner_sn_o").toString()));
				params3.put("name_i", form2.getAttachment_name());
				params3.put("url_i", form2.getAttachment_url());
				mapper.addContractAttachment(params3);
			}
			Map<String, Object> params4 = new HashMap<>();
			params4.put("partA", form.getPartA());
			params4.put("partB", form.getPartB());
			params4.put("contract_inner_sn", cid);
			return mapper.selectContractInfo(params4);
		} else {
			if ("1062".equalsIgnoreCase(params.get("error_code_o").toString())) {
				throw new DuplicateException(MessageUtils.message("db.add.fail", "合同", params.get("message_string_o")));
			} else {
				throw new DBException(MessageUtils.message("db.add.fail", "合同", params.get("message_string_o")));
			}
		}
	}

	@Override
	@Transactional
	public int editContract(EditContractForm form) {
		ContractMapper mapper = getMapper(form.getContract_type());
		if (form.isUpdate()) {
			Map<String, Object> info = form.getInfo();
			info.put("partA", form.getPartA());
			info.put("partB", form.getPartB());
			info.put("contract_inner_sn", form.getContract_inner_sn());
			try {
				mapper.updateContract(form.getInfo());
			} catch (Exception e) {
				if (e instanceof DuplicateKeyException) {
					throw new DuplicateException(MessageUtils.message("db.update.fail", "合同", "合同编码重复"));
				} else {
					throw new DBException(MessageUtils.message("db.update.fail", "合同", e.getMessage()));
				}
			}
		} 
		if(mapper instanceof HospitalDealerContractMapper || mapper instanceof HospitalVendorContractMapper) {
			if (StringUtils.isNotEmpty(form.getHospital_department_inner_sn())
					|| StringUtils.isNotEmpty(form.getHospital_department_inner_sn_old())) {
				// 删除旧部门
				Map<String, Object> params1 = new HashMap<>();
				params1.put("partA", form.getPartA());
				params1.put("partB", form.getPartB());
				params1.put("contract_inner_sn", form.getContract_inner_sn());
				mapper.deleteCRelHD(params1);
				// 添加新部门
				String[] dept_id = form.getHospital_department_inner_sn().split(",");
				for (String dept_innser_sn : dept_id) {
					if (StringUtils.isEmpty(dept_innser_sn)) continue;
					params1.put("department_inner_sn", Long.valueOf(dept_innser_sn));
					mapper.insertCRelHD(params1);
				}
			}
		}
	
		if (StringUtils.isNotEmpty(form.getProductList())) {
			Map<String, Object> params = new HashMap<>();
			params.put("partA", form.getPartA());
			params.put("partB", form.getPartB());
			params.put("contract_inner_sn", form.getContract_inner_sn());
	
			mapper.deleteAllContractProduct(params);
			if(form.getContract_type().equals(0) || form.getContract_type().equals(3)) {
				mapper.deleteAllContractDealerProduct(params);
			}
			List<ContractProductForm> productlist = form.getProductList();
			for (ContractProductForm productList : productlist) {
				try {
				params.put("product_type", productList.getProduct_type());
				params.put("product_vendor_inner_sn", productList.getProduct_vendor_inner_sn());
				params.put("product_vendor_name", productList.getProduct_vendor_name());
				params.put("product_inner_sn", productList.getProduct_inner_sn());
				params.put("product_name", productList.getProduct_name());
				params.put("specification_inner_sn", productList.getSpecification_inner_sn());
				params.put("specification", productList.getSpecification());
				params.put("unit_inner_sn", productList.getUnit_inner_sn());
				params.put("unit_name", productList.getUnit_name());
				params.put("price", productList.getPrice().multiply(new BigDecimal(10000)));
				if (productList.getProduct_type() == 1) {
					mapper.insertContractProduct(params);
				} else {
					mapper.insertContractDealerProduct(params);
				}
				} catch (Exception e) {
					if (e instanceof DuplicateKeyException) {
						String msg = String.format("'%s>%s>%s>%s'重复!", productList.getProduct_vendor_name(),productList.getProduct_name(),
								productList.getSpecification(),productList.getUnit_name());
						throw new DuplicateException(MessageUtils.message("db.update.fail", "合同产品", msg));
					} else {
						throw new DBException(MessageUtils.message("db.update.fail", "合同", e.getMessage()));
					}
				}
			}
		} else {
			Map<String, Object> params = new HashMap<>();
			params.put("partA", form.getPartA());
			params.put("partB", form.getPartB());
			params.put("contract_inner_sn", form.getContract_inner_sn());
			mapper.deleteAllContractProduct(params);
			if(form.getContract_type().equals(0) || form.getContract_type().equals(3)) {
				mapper.deleteAllContractDealerProduct(params);
			}
		}
	
		if (StringUtils.isNotEmpty(form.getAttachmentList())) {
			Map<String, Object> params = new HashMap<>();
			params.put("partA", form.getPartA());
			params.put("partB", form.getPartB());
			params.put("contract_inner_sn", form.getContract_inner_sn());
			mapper.deleteAllContractAttachment(params);
			List<ContractAttachmentForm> attachmentlist = form.getAttachmentList();
			Map<String, Object> params1 = new HashMap<>();
			for (ContractAttachmentForm attachmentList : attachmentlist) {
				params1.put("partA_i", form.getPartA());
				params1.put("partB_i", form.getPartB());
				params1.put("contract_inner_sn_i", form.getContract_inner_sn());
				params1.put("name_i", attachmentList.getAttachment_name());
				params1.put("url_i", attachmentList.getAttachment_url());
				mapper.addContractAttachment(params1);
			}
		}
	
		return 1;
	}

	@Override
	@Transactional
	public int deleteContract(ContractCIDForm form) {
		ContractMapper mapper = getMapper(form.getContract_type());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("partA", form.getPartA());
		params.put("partB", form.getPartB());
		params.put("contract_inner_sn", form.getContract_inner_sn());
		mapper.deleteAllContractProduct(params);
		if(form.getContract_type().equals(0) || form.getContract_type().equals(3)) {
			mapper.deleteAllContractDealerProduct(params);
		}
		if(mapper instanceof HospitalDealerContractMapper || mapper instanceof HospitalVendorContractMapper) {
			mapper.deleteCRelHD(params);
		}
		return mapper.deleteContract(params);
	}

	@Override
	public List<ContractForm> selectDraftContractList(ContractSearchForm form) {
		Long uid = ServletUtils.getCurUserID();
		SystemType systemType = ServletUtils.getCurUserSystemType();
		form.setStatus("0");
		form.setInputer_inner_sn(uid);
		List<ContractForm> contractList = new ArrayList<ContractForm>();
		switch (systemType) {
		case HOSPITAL:
			//获取医院2经销商
			setContractFormList(contractList, hdMapper.selectContractList(form));
			//获取医院2厂商
			setContractFormList(contractList, hvMapper.selectContractList(form));
			break;
		case DEALER:
			form.setPartB(form.getPartA());
			form.setPartA(null);
			//获取医院2经销商
			setContractFormList(contractList, hdMapper.selectContractList(form));
			//经销商2经销商
			setContractFormList(contractList, ddMapper.selectContractList(form));
			//经销商2厂商
			form.setPartA(form.getPartB());
			form.setPartB(null);
			setContractFormList(contractList, dvMapper.selectContractList(form));
			break;
		case VENDOR:
			form.setPartB(form.getPartA());
			form.setPartA(null);
			//获取医院2厂商
			setContractFormList(contractList, hvMapper.selectContractList(form));
			//获取经销商2厂商
			setContractFormList(contractList, dvMapper.selectContractList(form));
			break;
		default:
			break;
		}
		return contractList;
	}

	private void setContractFormList(List<ContractForm> contractList, List<Contract> contracts) {
		for(Contract ddc :contracts) {
			ContractForm form = new ContractForm();
			BeanUtils.copyProperties(ddc, form);
			
			contractList.add(form);
		}
	}
	
	private void setContractFormList2(List<ContractForm> contractList, List<Map<String, Object>> contracts) {
		for(Map<String, Object> ddc :contracts) {
			ContractForm form = new ContractForm();
			form.setContract_type(3);
			form.setPartA(Long.valueOf(ddc.get("partA").toString()));
			form.setPartA_name(ddc.get("partA_name").toString());
			form.setPartB(Long.valueOf(ddc.get("partB").toString()));
			form.setPartB_name(ddc.get("partB_name").toString());
			form.setContract_inner_sn(Long.valueOf(ddc.get("contract_inner_sn").toString()));
			form.setContract_sn(ddc.get("contract_sn").toString());
			form.setName(ddc.get("contract_name").toString());	
			form.setStatus(Integer.valueOf(ddc.get("status").toString()));
			form.setApprover_name(ddc.get("approver_name").toString());
			if(StringUtils.isNotNull(ddc.get("approver_inner_sn"))) {
				form.setApprover_inner_sn(Long.valueOf(ddc.get("approver_inner_sn").toString()));
			}else {
				form.setApprover_inner_sn(null);
			}
			form.setContract_type(Integer.valueOf(ddc.get("contract_type").toString()));
			contractList.add(form);
		}
	}
	
	@Override
	public List<Contract> selectContractList(SystemType systemType, ContractSearchForm form) {
		SystemType curSystemType = ServletUtils.getCurUserSystemType();//获取登录用户的系统类型
		switch (curSystemType) {
		case HOSPITAL:
			switch (systemType) {
			case DEALER:
				return hdMapper.selectContractList(form);
			case VENDOR:
				return hvMapper.selectContractList(form);
			default:
			}
			break;
		case DEALER:
			switch (systemType) {
			case HOSPITAL:
				Long orgId = form.getPartA();
				form.setPartA(null);
				form.setPartB(orgId);
				return hdMapper.selectContractList(form);
			case DEALER:
				return ddMapper.selectContractList(form);
			case VENDOR:
				return dvMapper.selectContractList(form);
			default:
			}
			break;
		case VENDOR:
			Long orgId = form.getPartA();
			form.setPartA(null);
			form.setPartB(orgId);
			switch (systemType) {
			case DEALER:
				return dvMapper.selectContractList(form);
			case HOSPITAL:
				return hvMapper.selectContractList(form);
			default:
			}
			break;
		default:
		}
		return new ArrayList<>();
	}

	@Override
	public Contract selectContractInfo(ContractCIDForm form) {
		ContractMapper mapper = getMapper(form.getContract_type());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("partA", form.getPartA());
		params.put("partB", form.getPartB());
		params.put("contract_inner_sn", form.getContract_inner_sn());
		Contract contract = mapper.selectContractInfo(params);
		List<ContractVendorProduct> details = mapper.selectAllContractProductList(params);
		contract.setDetails(details);
		List<ContractAttachment> attachments = mapper.selectContractAttachment(params);
		contract.setAttachments(attachments);
		return contract;
	}

	@Override
	@Transactional
	public int addContractApprove(AddContractApproveForm form) {
		ContractMapper mapper = getMapper(form.getContract_type());
		Map<String, Object> params = new HashMap<>();
		Long previous_approve_inner_sn = null;
//		Long approve_inner_sn = null;

		params.put("partA_i", form.getPartA());
		params.put("partB_i", form.getPartB());
		params.put("contract_inner_sn_i", form.getContract_inner_sn());
		// 先加入发起者为第一个审批人
		params.put("approver_inner_sn_i", ServletUtils.getCurUserID());
		params.put("approver_name_i", getCurUserEmployeeName());
		params.put("previous_approve_inner_sn_i", previous_approve_inner_sn);
		mapper.addContractApprove(params);
		if (Integer.valueOf(params.get("result_o").toString()) == 0) {

			previous_approve_inner_sn = Long.valueOf(params.get("approve_inner_sn_o").toString());

			ApproveContractForm ids = new ApproveContractForm();
			BeanUtils.copyProperties(form, ids);

			ids.setApprove_inner_sn(previous_approve_inner_sn);

			List<ApproverForm> approverList = form.getApprover_inner_sn();
			for (ApproverForm approver : approverList) {
				params.put("approver_inner_sn_i", approver.getApprover_inner_sn());
				params.put("approver_name_i", approver.getApprover_name());
				params.put("previous_approve_inner_sn_i", previous_approve_inner_sn);
				mapper.addContractApprove(params);
				if (Integer.valueOf(params.get("result_o").toString()) == 0) {
					previous_approve_inner_sn = Long.valueOf(params.get("approve_inner_sn_o").toString());
				} else {
					throw new DBException(MessageUtils.message("db.procedures.error", params.get("message_string_o")));
				}
			}
			startContractApprove(ids, mapper);
			return 1;
		} else {
			throw new DBException(MessageUtils.message("db.procedures.error", params.get("message_string_o")));
		}
	}
	
	@Override
	@Transactional
	public int addContractApproveComment(AddContractApproveCommentForm form) {
		ContractMapper mapper = getMapper(form.getContract_type());
		Map<String, Object> params = new HashMap<>();
		params.put("partA_i", form.getPartA());
		params.put("partB_i", form.getPartB());
		params.put("contract_inner_sn_i", form.getContract_inner_sn());
		params.put("approve_inner_sn_i", form.getApprove_inner_sn());
		params.put("comment_i", form.getComment());
		mapper.addContractApproveComment(params);
		if (Integer.valueOf(params.get("result_o").toString()) == 0) {
			return 1;
		} else {
			throw new DBException(MessageUtils.message("db.procedures.error", params.get("message_string_o")));
		}
	}
	
	@Override
	public List<ContractForm> selectContractApproveList(SystemType systemType, ContractSearchForm form) {
		Long uid = ServletUtils.getCurUserID();
		List<ContractForm> contractList = new ArrayList<ContractForm>();
		Map<String, Object> params = new HashMap<>();
		params.put("user_id", uid);
		params.put("name", form.getName());
		params.put("contract_sn", form.getContract_sn());
		params.put("partA_name", form.getPartA_name());
		params.put("partB_name", form.getPartB_name());
		switch (systemType) {
		case HOSPITAL:
			//获取医院2经销商
			setContractFormList2(contractList, hdMapper.selectContracctListByApprover(params));
			//获取医院2厂商
			setContractFormList2(contractList, hvMapper.selectContracctListByApprover(params));
			break;
		case DEALER:
			//获取医院2经销商
			setContractFormList2(contractList, hdMapper.selectContracctListByApprover(params));
			//经销商2经销商
			setContractFormList2(contractList, ddMapper.selectContracctListByApprover(params));
			//经销商2厂商
			setContractFormList2(contractList, dvMapper.selectContracctListByApprover(params));
			break;
		case VENDOR:
			//获取医院2厂商
			setContractFormList2(contractList, hvMapper.selectContracctListByApprover(params));
			//获取经销商2厂商
			setContractFormList2(contractList, dvMapper.selectContracctListByApprover(params));
			break;
		default:
		}
		return contractList;
	}
	
	@Override
	public List<ContractApproveForm> selectContractApproveCommentList(ContractCIDForm form){
		ContractMapper mapper = getMapper(form.getContract_type());
		Map<String, Object> params = new HashMap<>();
		params.put("partA", form.getPartA());
		params.put("partB", form.getPartB());
		params.put("contract_inner_sn", form.getContract_inner_sn());

		List<Map<String, Object>> resultList = mapper.selectContractApproveCommentList(params);
		List<ContractApproveForm> outResultList = new ArrayList<ContractApproveForm>();
		for (Map<String, Object> result : resultList) {
			ContractApproveForm hdcaf = new ContractApproveForm();
			hdcaf.setPartA(Long.valueOf(result.get("partA").toString()));
			hdcaf.setPartB(Long.valueOf(result.get("partB").toString()));
			hdcaf.setContract_inner_sn(Long.valueOf(result.get("contract_inner_sn").toString()));
			hdcaf.setApprove_inner_sn(Long.valueOf(result.get("approve_inner_sn").toString()));

			Long previous_approve_inner_sn = StringUtils.isNull(result.get("previous_approve_inner_sn")) ? null
					: Long.valueOf(result.get("previous_approve_inner_sn").toString());
			Long approver_inner_sn = StringUtils.isNull(result.get("approver_inner_sn")) ? null
					: Long.valueOf(result.get("approver_inner_sn").toString());
			Integer status = StringUtils.isNull(result.get("status")) ? null
					: Integer.valueOf(result.get("status").toString());

			hdcaf.setPrevious_approve_inner_sn(previous_approve_inner_sn);
			hdcaf.setStatus(status);
			hdcaf.setApprover_inner_sn(approver_inner_sn);
			hdcaf.setApprover_name(String.valueOf(result.get("approver_name")));

			if (result.get("approve_time") != null)
				hdcaf.setApprove_time((Date) result.get("approve_time"));

			if (result.get("description") != null)
				hdcaf.setDescription(result.get("description").toString());

			ContractApproveCommentForm hdcac = new ContractApproveCommentForm();
			if (result.get("comment") != null) {
				hdcac.setComment(result.get("comment").toString());
				hdcac.setComment_inner_sn(Long.valueOf(result.get("comment_inner_sn").toString()));
				hdcac.setComment_time((Date) result.get("comment_time"));
			}
			int index = contractContain(outResultList, hdcaf);
			List<ContractApproveCommentForm> commentList = new ArrayList<>();
			if (index == -1) {
				if (hdcac.getComment() != null) {
					commentList.add(hdcac);
					hdcaf.setApproveComment(commentList);
				}
				outResultList.add(hdcaf);
			} else {
				if (hdcac.getComment() != null) {
					commentList = outResultList.get(index).getApproveComment();
					commentList.add(hdcac);
					hdcaf.setApproveComment(commentList);
				}
			}
		}
		return outResultList;
	}

	// 合同审批同意
	@Override
	@Transactional
	public int agreeContractApprove(ApproveContractForm form) {
		ContractMapper mapper = getMapper(form.getContract_type());
		Map<String, Object> params = new HashMap<>();
		params.put("partA", form.getPartA());
		params.put("partB", form.getPartB());
		params.put("contract_inner_sn", form.getContract_inner_sn());
		params.put("approve_inner_sn", form.getApprove_inner_sn());
		params.put("status", 3);
		params.put("approve_time", new Date());
		params.put("description", form.getDescription());
		mapper.updateContractApprove(params);

		String str = null;
		str = mapper.selectContractApproveNextId(params);

		if (str == null) {// 如果该环节为最后一个审批环节 则修改合同状态为审批通过
			Map<String, Object> params1 = new HashMap<>();
			params1.put("partA", form.getPartA());
			params1.put("partB", form.getPartB());
			params1.put("contract_inner_sn", form.getContract_inner_sn());
			params1.put("approve_inner_sn", form.getApprove_inner_sn());
			params1.put("description", form.getDescription());
			Contract info = mapper.selectContractInfo(params);
			Date d1 = info.getBegin_date();
			Date d2 = new Date();
			if (d1.getTime() <= d2.getTime()) {// 判断合同开始时间是否小于审批时间，如果是则合同状态应该为有效履行中
				params1.put("status", 4);
				;// 4-有效履行中
			} else {
				params1.put("status", 3);
				;// 3-审批通过
			}
			return mapper.updateContract(params1);
		} else {// 将下一个审批环节置为处理中
			params.put("approve_inner_sn", Long.valueOf(str));
			params.put("status", 2);
			params.remove("description");
			params.remove("approve_time");
			return mapper.updateContractApprove(params);
		}
	}

	// 合同审批不同意
	@Override
	@Transactional
	public int disagreeContractApprove(ApproveContractForm form) {
		ContractMapper mapper = getMapper(form.getContract_type());
		Map<String, Object> params = new HashMap<>();
		params.put("partA", form.getPartA());
		params.put("partB", form.getPartB());
		params.put("contract_inner_sn", form.getContract_inner_sn());
		params.put("approve_inner_sn", form.getApprove_inner_sn());
		params.put("status", 4);
		params.put("approve_time", new Date());
		params.put("description", form.getDescription());
		mapper.updateContractApprove(params);
		// 讲合同状态置为审核不通过
		Map<String, Object> params1 = new HashMap<>();
		params1.put("partA", form.getPartA());
		params1.put("partB", form.getPartB());
		params1.put("contract_inner_sn", form.getContract_inner_sn());
		params1.put("approve_inner_sn", form.getApprove_inner_sn());
		params1.put("description", form.getDescription());
		params1.put("status", 2);
		return mapper.updateContract(params1);
	}

	// 合同审批待定
	@Override
	@Transactional
	public int holdContractApprove(ApproveContractForm form) {
		ContractMapper mapper = getMapper(form.getContract_type());
		Map<String, Object> params = new HashMap<>();
		params.put("partA", form.getPartA());
		params.put("partB", form.getPartB());
		params.put("contract_inner_sn", form.getContract_inner_sn());
		params.put("approve_inner_sn", form.getApprove_inner_sn());
		params.put("status", 2);
		params.put("description", form.getDescription());
		return mapper.updateContractApprove(params);
	}

	@Override
	@Transactional
	public int breakOffContract(ContractCIDForm form) {
		ContractMapper mapper = getMapper(form.getContract_type());
		Map<String, Object> info = new HashMap<>();
		info.put("partA", form.getPartA());
		info.put("partB", form.getPartB());
		info.put("contract_inner_sn", form.getContract_inner_sn());
		info.put("status", 6);//6-提前终止
		return mapper.updateContract(info);
	}

	@Override
	public List<Map<String, Object>> analyseContract(@Validated AnalyseForm form) {
		Integer type = form.getContract_type();
		switch (type) {
		case 0://hospitalDealer
			return new ArrayList<>();//缺设计，没完成
		case 1://hospitalVendor
			return new ArrayList<>();//缺设计，没完成
		}
		return new ArrayList<>();
	}
	
	@Autowired ProductUnitMapper puMapper;
	@Autowired DealerProductUnitMapper dpuMapper;

	@Override
	public List<BigDecimal> getContractPrice(@Validated SearchContractPriceForm form) {
		List<ContractProduct> list = null;
		if(BillsType.VV.equals(form.getBillsType())) {
			return new ArrayList<>();
		}
		if (form.getIsLast()){
			OdoMapper odoMapper = getOdoMapper(form.getBillsType());
			Map<String, Object> params = new HashMap<>();
			params.put("org_id", ServletUtils.getCurUserOrgID());
			params.put("organization_inner_sn",form.getOrganization_inner_sn());
			params.put("product_inner_sn", form.getProduct_inner_sn());
			params.put("specification_inner_sn", form.getSpecification_inner_sn());
			params.put("product_vendor_inner_sn",form.getProduct_vendor_inner_sn());

			list = odoMapper.selectLastPrice(params);
		}
		
		if(list == null || Utils.isEmpty(list)) {
			BillsType type = form.getBillsType();
			int product_type = form.getProduct_type();
			Long org_id = ServletUtils.getCurUserOrgID();
			Map<String, Object> params1 = new HashMap<>();
			params1.put("org_id", form.getSaleUse()?form.getOrganization_inner_sn():org_id);
			params1.put("organization_inner_sn", form.getSaleUse()?org_id:form.getOrganization_inner_sn());
			params1.put("product_vendor_inner_sn", form.getProduct_vendor_inner_sn());
			params1.put("product_inner_sn", form.getProduct_inner_sn());
			params1.put("specification_inner_sn", form.getSpecification_inner_sn());
			switch (type) {
			case DD:
				if(product_type == 1) {
					list = ddMapper.selectContractProductList(params1);
				}else {
					list = ddMapper.selectContractDealerProductList(params1);
				}
				break;
			case DV:
			case VD:
				if(product_type == 1) {
					list = dvMapper.selectContractProductList(params1);
				}else {
					list = dvMapper.selectContractDealerProductList(params1);
				}
				break;
			case HV:
			case VH:
				if(product_type == 1) {
					list = hvMapper.selectContractProductList(params1);
				}else {
					list = hvMapper.selectContractDealerProductList(params1);
				}
				break;
			case HD:
			case DH:
				if(product_type == 1) {
					list = hdMapper.selectContractProductList(params1);
				}else {
					list = hdMapper.selectContractDealerProductList(params1);
				}
				break;
			default:
				//不支持的类型返回空列表，杨韬要求的
	//			throw new DBException("当前机构不支持该功能!");
				return new ArrayList<>();
			}
		}
		List<BigDecimal> result = new ArrayList<>();
		for (ContractProduct product : list) {
			BigDecimal curPrice = BigDecimal.ZERO;
			Map<String, Object> params = new HashMap<>();
			params.put("vendor_inner_sn_i", product.getProduct_vendor_inner_sn());
			params.put("product_inner_sn_i", product.getProduct_inner_sn());
			params.put("specification_inner_sn_i", product.getSpecification_inner_sn());
			params.put("unit_inner_sn_i", product.getUnit_inner_sn());
			if(product.getProduct_type().equals(1)) {
				BigDecimal tmp = puMapper.calculate_product_min_unit(params);
				BigDecimal minPrice = product.getPrice().divide(tmp, 4, BigDecimal.ROUND_HALF_UP);//合同的包装单位的价格转换为最小包装单位的价格
				if(StringUtils.isNotNull(minPrice)) {
					params.put("unit_inner_sn_i", form.getUnit_inner_sn());
					curPrice = minPrice.multiply(puMapper.calculate_product_min_unit(params));//最小包装单位价格转换为参数单位的价格
				}
			}else {
				BigDecimal tmp = dpuMapper.calculate_product_min_unit(params);
				BigDecimal minPrice = product.getPrice().divide(tmp, 4, BigDecimal.ROUND_HALF_UP);
				if(StringUtils.isNotNull(minPrice)) {
					params.put("unit_inner_sn_i", form.getUnit_inner_sn());
					curPrice = minPrice.multiply(dpuMapper.calculate_product_min_unit(params));//最小包装单位价格转换为参数单位的价格
				}
			}
			result.add(curPrice);
		}
		return result;
	}

	@Override
	public List<SearchContractDetail> searchContractDetailForm(SystemType type,
			@Validated SearchContractDetailForm form) {
		Long orgId = ServletUtils.getCurUserOrgID();
		Map<String, Object> params = new HashMap<>();
		params.put("org_id", orgId);
		params.put("product_name", form.getProduct_name());
		params.put("product_vendor_name", form.getProduct_vendor_name());
		params.put("specification", form.getSpecification());
		
		switch (type) {
		case HOSPITAL:
			return hdMapper.searchContractDetail(params);
		case DEALER:
			return ddMapper.searchContractDetail(params);
		case VENDOR:
			return dvMapper.searchContractDetail(params);
		default:
			throw new DBException("当前机构不支持该功能!");
		}
	}

	private int contractContain(List<ContractApproveForm> list, ContractApproveForm hdcaf) {
		for (ContractApproveForm hdc : list) {
			if (hdc.getApprove_inner_sn() == hdcaf.getApprove_inner_sn())
				return list.indexOf(hdc);
		}
		return -1;
	}
	
	@Transactional
	private void startContractApprove(ApproveContractForm ids, ContractMapper mapper) {
		Map<String, Object> params = new HashMap<>();
		params.put("partA", ids.getPartA());
		params.put("partB", ids.getPartB());
		params.put("contract_inner_sn", ids.getContract_inner_sn());
		params.put("approve_inner_sn", ids.getApprove_inner_sn());
		params.put("status", 0);
		params.put("approve_time", new Date());
		params.put("description", ids.getDescription());
		mapper.updateContractApprove(params);

		Map<String, Object> params1 = new HashMap<>();
		params1.put("partA", ids.getPartA());
		params1.put("partB", ids.getPartB());
		params1.put("contract_inner_sn", ids.getContract_inner_sn());
		params1.put("approve_inner_sn", ids.getApprove_inner_sn());
		params1.put("description", ids.getDescription());
		params1.put("status", 1);
		mapper.updateContract(params1);

		Object next_approve_inner_sn = mapper.selectContractApproveNextId(params);

		if (next_approve_inner_sn != null) {// 将下一个审批环节置为处理中
			params.put("approve_inner_sn", next_approve_inner_sn);
			params.put("status", 2);
			params.remove("description");
			params.remove("approve_time");
			mapper.updateContractApprove(params);
		}
	}
}
