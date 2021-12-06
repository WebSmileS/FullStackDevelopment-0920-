package com.yrt.project.api.platform.service;

import java.util.List;

import com.yrt.project.api.platform.vo.organization.AddOrgRelImageForm;
import com.yrt.project.modular.organization.domain.OrgRelImage;
import com.yrt.project.modular.organization.domain.SearchForm;

public interface IOrgService {

	public List<?> searchOrgList(SearchForm form);
	
	public OrgRelImage insertOrgRelImage(Long id, int certType, AddOrgRelImageForm form);
	
	public List<OrgRelImage> getImageListByOrgId(Long id);
	
	public List<OrgRelImage> selectCertImageList2(Long id, int certType);
	
	public int deleteCertImage(Long id, int certType, int iid);
	
	public String getCertTypeName(int certType);
}
