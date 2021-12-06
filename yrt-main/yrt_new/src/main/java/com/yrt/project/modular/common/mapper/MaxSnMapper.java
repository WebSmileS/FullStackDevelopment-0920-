package com.yrt.project.modular.common.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.jdbc.SQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import lombok.Data;

/**
 * 生成最大编号(max_value).
 * <p>
 * 所有满足(key-columns, max_value number)的表： 即
 * 除max_value(Long以内的整型)外的所有列均为主键；max_value递增(从1开始)。
 * <p>
 * 所有 XXX_inner_sn_max, 及voucher_sn可用。
 */
@Repository
public interface MaxSnMapper {

	/**
	 * allocate ONE SN
	 * 
	 * @param tableName
	 * @param keys
	 * @return
	 */
	default long getMaxSn(String tableName, Map<String, Object> keys) {
		final int ONE = 1;
		return getMaxSn(tableName, keys, ONE);
	}

	/**
	 * allocate one or more SN(which specified by <code>size</code>), return the last
	 * SN.
	 * 
	 * @param tableName
	 * @param keys
	 * @param size ge 1
	 * @return n, result SNs(n-size+1...n)
	 */
	default long getMaxSn(String tableName, Map<String, Object> keys, int size) {
		final Logger logger = LoggerFactory.getLogger(MaxSnMapper.class);
		MaxSnParam param = MaxSnParam.of(tableName, keys, size);
		int r = incMaxSn(param);
		logger.info("maxSn: r={}, sn={}", r, param);
		return param.getMaxValue();
	}

	/**
	 * client use {@link #getMaxSn(String, Map)} or
	 * {@link #getMaxSn(String, Map, int)}
	 * 
	 * @param param
	 * @return 1(inserted), 2(updated).
	 */
	// @Transactional(propagation=Propagation.REQUIRES_NEW)
	@InsertProvider(type = SqlProvider.class, method = "incMaxSn")
	@SelectKey(statement = "select LAST_INSERT_ID()", keyProperty = "maxValue", before = false, resultType = long.class)
	int incMaxSn(MaxSnParam param);

	static class SqlProvider {
		public String incMaxSn(MaxSnParam param) {
			// INSERT into tableName values
			// (#{keyField1},...#{keyFieldN},LAST_INSERT_ID(#{size}))
			// ON DUPLICATE KEY UPDATE max_value = LAST_INSERT_ID(max_value + #{size})
			Map<String, Object> keys = param.keys;
			SQL sql = new SQL();
			sql.INSERT_INTO(param.tableName);

			keys.forEach((k, v) -> sql.VALUES(k, String.format("#{keys.%s}", k)));
			sql.VALUES("max_value", "LAST_INSERT_ID(#{size})");

			return sql.toString() + " ON DUPLICATE KEY UPDATE max_value=LAST_INSERT_ID(max_value + #{size})";
		}
	}

	@Data(staticConstructor = "of")
	static class MaxSnParam {
		private final String tableName;
		private final Map<String, Object> keys;
		private final int size;
		private long maxValue; // result value

		private MaxSnParam(String tableName, Map<String, Object> keys, int size) {
			if (size < 1) {
				throw new IllegalArgumentException("size can't be less than 1.");
			}
			this.tableName = tableName;
			this.keys = keys;
			this.size = size;
		}
	}
}
