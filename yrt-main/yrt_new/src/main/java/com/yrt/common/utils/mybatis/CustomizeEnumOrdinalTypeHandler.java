package com.yrt.common.utils.mybatis;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import com.yrt.common.dict.BaseIntEnum;

/**
 * same as built-in EnumOrdinalTypeHandler, and take care of
 * {@link com.yrt.common.dict.BaseIntEnum#value()} implement.
 */
public class CustomizeEnumOrdinalTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> { // EnumOrdinalTypeHandler

	private final Class<E> type;
	private final E[] enums;

	public CustomizeEnumOrdinalTypeHandler(Class<E> type) {
		if (type == null) {
			throw new IllegalArgumentException("Type argument cannot be null");
		}
		this.type = type;
		this.enums = type.getEnumConstants();
		if (this.enums == null) {
			throw new IllegalArgumentException(type.getSimpleName() + " does not represent an enum type.");
		}
	}

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
		ps.setInt(i, BaseIntEnum.value(parameter));
	}

	@Override
	public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
		Object o = rs.getObject(columnName);
		if (rs.wasNull()) {
			return null;
		} else {
			try {
				return o instanceof Number ? BaseIntEnum.valueOf(type,((Number) o).intValue()): Enum.valueOf(type, o.toString());
			} catch (Exception ex) {
				throw new IllegalArgumentException(
						"Cannot convert " + o + " to " + type.getSimpleName() + " by ordinal value.", ex);
			}
		}
	}

	@Override
	public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		Object o = rs.getObject(columnIndex);
		if (rs.wasNull()) {
			return null;
		} else {
			try {
				return o instanceof Number ? BaseIntEnum.valueOf(type,((Number) o).intValue()): Enum.valueOf(type, o.toString());
			} catch (Exception ex) {
				throw new IllegalArgumentException(
						"Cannot convert " + o + " to " + type.getSimpleName() + " by ordinal value.", ex);
			}
		}
	}

	@Override
	public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		Object o = cs.getObject(columnIndex);
		int i = o instanceof Number ? ((Number) o).intValue() : -1;
		if (cs.wasNull()) {
			return null;
		} else {
			try {
				return BaseIntEnum.valueOf(type, i);
			} catch (Exception ex) {
				throw new IllegalArgumentException(
						"Cannot convert " + i + " to " + type.getSimpleName() + " by ordinal value.", ex);
			}
		}
	}

}
