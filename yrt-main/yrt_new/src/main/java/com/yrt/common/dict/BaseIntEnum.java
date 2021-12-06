package com.yrt.common.dict;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Enum implement this interface to customize:
 * <ol>
 * <li>{@link #value()} real numeric into DB. Default impl Enum's
 * {@link #ordinal()}</li>
 * <li>{@link #text()} description text(display for end user). Default impl
 * Enum's {@link #toString()}</li>
 * </ol>
 * 
 * <p>
 */
public interface BaseIntEnum {
	// enum methods///////////////////////////////////////////
	String name();
	int ordinal();
	/////////////////////////////////////////////////////////

	// customize value/text /////////////////////////////////////////
	/**
	 * actual value(db stored).
	 * 
	 * @return
	 */
	default int value() {
		return this.ordinal();
	}

	/**
	 * description text(for end user)
	 * 
	 * @return
	 */
	default String text() {
		return this.toString();
	}
	////////////////////////////////////////////////////////////////

	/**
	 * Deserialize from numeric value
	 * 
	 * @param enumType
	 * @param value
	 * @return
	 */
	public static <T extends Enum<?>> T valueOf(Class<T> enumType, int value) {
		T[] values = enumType.getEnumConstants();

		T result = null;
		if (BaseIntEnum.class.isAssignableFrom(enumType)) {
			for (T e : values) {
				if (((BaseIntEnum) e).value() == value) {
					result = e;
					break;
				}
			}
		} else if (value >= 0 && value < values.length) {
			result = values[value];
		}

		if (result == null) {
			throw new IllegalArgumentException("Unknown enum constant " + enumType.getCanonicalName() + ": " + value);
		}
		return result;
	}

	/**
	 * get int-value by value(if implements this interface ) otherwise ordinal()
	 * 
	 * @param e
	 * @return
	 */
	public static <T extends Enum<?>> int value(T e) {
		if (e instanceof BaseIntEnum) {
			return ((BaseIntEnum) e).value();
		} else {
			return e.ordinal();
		}
	}

	/**
	 * key: enum element's name, value: enum element's text/toString
	 * 
	 * @param enumType
	 * @return
	 */
	public static <T extends Enum<?>> Map<String, String> mapElements(Class<T> enumType) {
		T[] values = enumType.getEnumConstants();
		if (values == null) {
			return Collections.emptyMap();
		}
		Map<String, String> map = new HashMap<>(values.length);
		if (BaseIntEnum.class.isAssignableFrom(enumType)) {
			for (T e : values) {
				map.put(e.name(), ((BaseIntEnum) e).text());
			}
		} else {
			for (T e : values) {
				map.put(e.name(), e.toString());
			}
		}
		return map;
	}

	/**
	 * 
	 * @param enumType
	 * @return
	 */
	public static <T extends Enum<?>> List<EnumItemDesc> listElements(Class<T> enumType) {
		T[] values = enumType.getEnumConstants();
		if (values == null) {
			return Collections.emptyList();
		}
		List<EnumItemDesc> list = new ArrayList<>(values.length);
		if (BaseIntEnum.class.isAssignableFrom(enumType)) {
			for (T e : values) {
				list.add(EnumItemDesc.of(e.name(), ((BaseIntEnum) e).text()));
			}
		} else {
			for (T e : values) {
				list.add(EnumItemDesc.of(e.name(), e.toString()));
			}
		}
		return list;
	}

	static public class EnumItemDesc {

		public static EnumItemDesc of(String name, String text) {
			return new EnumItemDesc(name, text);
		}

		private String name;
		private String text;

		public EnumItemDesc(String name, String text) {
			this.name = name;
			this.text = text;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

	}
}
