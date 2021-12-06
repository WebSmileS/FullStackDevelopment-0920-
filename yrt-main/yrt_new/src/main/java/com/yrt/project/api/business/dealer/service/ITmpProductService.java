package com.yrt.project.api.business.dealer.service;

import java.util.List;

import com.yrt.common.exception.DBException;
import com.yrt.common.exception.DuplicateException;
import com.yrt.framework.web.page.TableDataInfo;
import com.yrt.project.api.business.dealer.vo.tmpproduct.AddTmpProductForm;
import com.yrt.project.api.business.dealer.vo.tmpproduct.AddTmpProductImageForm;
import com.yrt.project.api.business.dealer.vo.tmpproduct.AddTmpProductMDRFForm;
import com.yrt.project.api.business.dealer.vo.tmpproduct.AddTmpProductSpecForm;
import com.yrt.project.api.business.dealer.vo.tmpproduct.AuditForm;
import com.yrt.project.api.business.dealer.vo.tmpproduct.EditTmpProductForm;
import com.yrt.project.api.business.dealer.vo.tmpproduct.EditTmpProductMDRFForm;
import com.yrt.project.api.business.dealer.vo.tmpproduct.EditTmpProductSpecForm;
import com.yrt.project.api.business.dealer.vo.tmpproduct.ImageIdsForm;
import com.yrt.project.api.business.dealer.vo.tmpproduct.TmpIDSForm;
import com.yrt.project.api.business.dealer.vo.tmpproduct.TmpProductComposeForm;
import com.yrt.project.api.business.dealer.vo.tmpproduct.TmpSearchProductForm;
import com.yrt.project.modular.product.domain.TmpProduct;
import com.yrt.project.modular.product.domain.TmpProductMDRF;
import com.yrt.project.modular.product.domain.TmpProductRelImage;
import com.yrt.project.modular.product.domain.TmpProductSpecification;

public interface ITmpProductService {

	//待审核产品
	public TableDataInfo<TmpProduct> searchtTmpProduct(TmpSearchProductForm form, Integer pageNum, Integer pageSize);
	
	public TmpProduct getTmpProductInfo(TmpIDSForm form);
	
	public TmpProduct addTmpProduct(AddTmpProductForm form);
	
	public TmpProduct addOrEditTmpProductCompose(TmpProductComposeForm form, boolean isUpdate);
	
	public TmpProduct editTmpProduct(EditTmpProductForm form);
	
	public int deleteTmpProudct(TmpIDSForm form);
	
	//待审核规格型号
	public TmpProductSpecification addTmpProductSpec(AddTmpProductSpecForm form) throws DBException, DuplicateException;
	
	public int editTmpProductSpec(EditTmpProductSpecForm form) throws DBException, DuplicateException;
	
	public int deleteTmpProductSpec(TmpIDSForm form);
	
	public List<TmpProductSpecification> selectTmpProductSpecList(TmpIDSForm form);
	
	public TmpProductSpecification selectTmpProductSpecInfo(TmpIDSForm form);
	
	//图片
	public TmpProductRelImage addTmpProductRelImage(AddTmpProductImageForm form);
	
	public int delTmpProductImage(ImageIdsForm form);
	
	public List<TmpProductRelImage> selectCertImageList(ImageIdsForm form);
	
	//注册证
	public TmpProductMDRF addTmpProductMDRF(AddTmpProductMDRFForm form);
	
	public int editTmpProductMDRF(EditTmpProductMDRFForm form);
	
	public int delTmpProductMDRF(TmpIDSForm form);
	
	public TmpProductMDRF getTmpProductMDRF(TmpIDSForm form);
	
	public List<TmpProductMDRF> getTmpProductMDRFList(TmpIDSForm form);

	//审批
	public int sendAudit(AuditForm form);

}
