package com.yrt.system.param.mapper;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import com.yrt.common.dict.SystemType;
import com.yrt.common.utils.Utils;
import com.yrt.system.param.domain.SysParameter;

@Repository
public interface SysParameterMapper {
	
	default String getCurOrgSystParamValue(SystemType systemType, Long organizationId, String paramKey) {
		SysParameter param = SysParameter.of(systemType, organizationId, paramKey, "", null);
		String ret = selectOrgSysParameterValue(param);

		return Utils.isEmpty(ret) ? null : ret;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// mybatis sql semantic methods
	
	@InsertProvider(type = SqlProvider.class, method = "insertOrUpdateSysParameter")
	public void insertOrUpdateSysParameter(SysParameter param);
	
	@SelectProvider(type = SqlProvider.class, method = "selectOrgSysParameterValue")
	public @Nullable String selectOrgSysParameterValue(SysParameter param);
	
	static class SqlProvider {
		public String insertOrUpdateSysParameter(SysParameter param) {
			SQL sql = new SQL();
			sql.INSERT_INTO("system_parameter");
			sql.VALUES("system_type", "#{systemType}");
			sql.VALUES("organization_inner_sn", "#{organizationId}");
			sql.VALUES("param_key", "#{paramKey}");
			sql.VALUES("param_name", "#{paramName}");
			sql.VALUES("param_value", "#{paramValue}");
			
			return sql.toString() + " ON DUPLICATE KEY UPDATE param_name = #{paramName}, param_value = #{paramValue}";
		}
		
		public String selectOrgSysParameterValue(SysParameter param) {
			SQL sql = new SQL();
			sql.SELECT("param_value").FROM("system_parameter");
			sql.WHERE("system_type = #{systemType}");
			sql.WHERE("organization_inner_sn = #{organizationId}");
			sql.WHERE("param_key = #{paramKey}");
			
			return sql.toString();
		}
	}
}
